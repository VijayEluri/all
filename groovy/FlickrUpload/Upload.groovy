import java.io.File;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.SearchParameters;

void uploadFile(file, title, sender) throws Exception
{
	println "\tUploading " + file.getAbsolutePath();
	Flickr flickr = FlickrFactory.getInstance().getFlickr();
	SearchParameters para = new SearchParameters();
	para.setUserId(FlickrFactory.getInstance().getUser().getId());
	para.setText(title);

	para.setSort(SearchParameters.DATE_POSTED_DESC);
	PhotoList photoList = flickr.getPhotosInterface().search(para, 100, 1);
	if (photoList.size() == 0) {
		print "\t\tUploading...";
		sender.send(title, file);
		println 'done' 
	} else {
		println "\tFound it uploaded.";
	}
}

void processFolder(file, sender)
{
	if (!file.isDirectory())
		return;
	File[] files = file.listFiles();
	for (int i = 0; i < files.length; ++i) {
		File f = files[i];
		if (f.isDirectory()) {
			processFolder(f);
		} else {
			String fullPath = f.getAbsolutePath();
			if (fullPath.toLowerCase() =~ /.*\.jpg$/) {
				File jpgFile = new File(fullPath);
				String fileName = jpgFile.getName().substring(0, jpgFile.getName().lastIndexOf("."));
				try {
          uploadFile(jpgFile, fileName.toUpperCase(), sender);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

def run(dir, sender)
{
	println "Processing " + dir + " ...";
	File file = new File(dir);
	processFolder(file, sender);
	println "done.";
}

if (args.length < 1) {
	println "Upload <dir to upload> [gmail | corp]";
	return;
}

def dir = args[0];
def sender = 'gmail'
def mailsender

if (args.length == 2) {
  sender = args[1]
}

println "Sending via ${sender}"

Upload u = new Upload();

if (sender == 'gmail')
  mailsender = new GmailSender()
else
  mailsender = new CorpSender()

u.run(dir, mailsender);
