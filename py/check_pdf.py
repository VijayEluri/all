import sys
import os
import shlex
import subprocess

def usage():
    print "Usage: %s <src dir>" % sys.argv[0]
    exit(1)

def mdls(path):
    cmd = "./num.sh %s" % (path)
    output = subprocess.Popen(shlex.split(cmd), stdout=subprocess.PIPE).communicate()[0]
    try:
        return int(output.strip())
    except:
        return -1

def process_dir(top):
    for name in os.listdir(top):
        file_path = os.path.join(top, name)
        if not os.path.isfile(file_path):
            continue
        if not name.endswith('.pdf'):
            continue
        yield (top, name)
            
def check_pdf(src_path):
    count = mdls(src_path)
    print count,
    if (count < 0) or (count > 100):
        print "pages: %d" % count
        try:
            os.mkdir('err')
        except:
            pass
        os.system("mv %s err/" % src_path)
                
if __name__ == '__main__':
    if len(sys.argv) < 2:
        usage()

    src = sys.argv[1]
        
    for (folder, name) in process_dir(src):
        src_path = os.path.join(folder, name)
        print "checking %s ... " % (src_path),
        check_pdf(src_path)
        print "done."