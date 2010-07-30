use warnings;
use strict;
use File::Find;
use Digest::MD5;

my @FILELIST = ();

sub process_file {
    return unless -f;
    my $file = $File::Find::name;
    push (@FILELIST, "$file");
}

my @DIRLIST = (".");
find(\&process_file, @DIRLIST);

@FILELIST = sort @FILELIST;

my $FILECOUNT = @FILELIST;
my %FILESIZE = ();
my %FILEHASH = ();
my %FILEOBJECTID = ();

sub file_size {
    my $file = shift;
    if (defined($FILESIZE{$file})) {
        return $FILESIZE{$file};
    }
    my $size = (stat($file))[7];
    $FILESIZE{$file} = $size;
    return $size;
}

sub file_hash {
    my $file = shift;
    if (defined($FILEHASH{$file})) {
        return $FILEHASH{$file};
    }

    open(FILE, $file) or die "Can't open '$file': $!";
    binmode(FILE);

    my $md5 = Digest::MD5->new;
    while (<FILE>) {
        $md5->add($_);
    }
    close(FILE);

    my $hash = $md5->b64digest;
    $FILEHASH{$file} = $hash;
    return $hash;
}

sub file_object_id {
    my $file = shift;
    if (defined($FILEOBJECTID{$file})) {
        return $FILEOBJECTID{$file};
    }

    my $cmd = "fsutil objectid query \"$file\"";
    my @res = `$cmd`;

    if ($res[0] =~ /The specified file has no object id/) {
        $cmd = "fsutil objectid create \"$file\"";
        @res = `$cmd`;
    }

    if ($res[0] =~ /Object ID :\s+(\S+)/) {
        $FILEOBJECTID{$file} = $1;
        return $1;
    }
}

sub de_dupe {
    my $file1 = shift;
    my $file2 = shift;

    my $id1 = file_object_id($file1);
    my $id2 = file_object_id($file2);

    if ($id1 eq $id2) {
    } else {
        print "$file1 and $file2 are real duped\n";
        unlink $file2;
        my $cmd = "fsutil hardlink create \"$file2\" \"$file1\"";
        print `$cmd`;
    }
}

sub is_dupe {
    my $file1 = shift;
    my $file2 = shift;
    
    if (file_size($file1) == 0) {
      print "$file1 is zero\n";
      return;
    }

    if (file_size($file1) eq file_size($file2)) {
        if (file_hash($file1) eq file_hash($file2)) {
            print "$file1 == $file2\n";
#            de_dupe($file1, $file2);
        }
    }
}

sub find_dupe {
    my $start = shift;
    my $index = $start + 1;
    while ($index < $FILECOUNT) {
        is_dupe($FILELIST[$start], $FILELIST[$index]);
        $index = $index + 1;
    } 
}

my $current = 0;

while($current < $FILECOUNT) {
    find_dupe($current);
    $current = $current + 1;
}
