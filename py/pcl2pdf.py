import sys
import os
import shlex
import subprocess

def usage():
    print "Usage: %s <src dir> <dst dir>" % sys.argv[0]
    exit(1)

def process_dir(top):
    for folder in os.listdir(top):
        folder_path = os.path.join(top, folder)
        if not os.path.isdir(folder_path):
            continue
        for name in os.listdir(folder_path):
            file_path = os.path.join(folder_path, name)
            if not os.path.isfile(file_path):
                continue
            if not name.endswith('.pcl'):
                continue
            yield (folder, name)
            
def convert_pdf(src_path, dst_path):
    cmd = "pcl6 -dNOPAUSE -sDEVICE=pdfwrite -sOutputFile=%s %s 2> /dev/null" % (dst_path, src_path)
    os.system(cmd)
                
if __name__ == '__main__':
    if len(sys.argv) < 3:
        usage()

    (src, dst) = sys.argv[1:3]
    try:
        os.mkdir(dst)
    except:
        pass
    
    i = 0
    for (folder, name) in process_dir(src):
        src_path = os.path.join(src, folder, name)
        dst_path = os.path.join(dst, "%s-%s.pdf" % (folder, name))
        i = i + 1
        print "%d: %s -> %s ... " % (i, src_path, dst_path),
        convert_pdf(src_path, dst_path)
        print "done."