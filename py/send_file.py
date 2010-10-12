import sys
import socket
import logging

logger = logging.getLogger('send_file')

def connect_target(host, port):
    (soc_family, _, _, _, address) = socket.getaddrinfo(host, port)[0]
    target = socket.socket(soc_family)
    target.connect(address)
    logger.info("connected to target %s:%d" % (host, port))
    
    return target

def usage():
    print "Usage: %s <file name> <target ip> <target port>" % sys.argv[0]
    exit(1)

if __name__ == '__main__':
    if (len(sys.argv) < 4):
        usage()
    filename = sys.argv[1]
    host = sys.argv[2]
    port = int(sys.argv[3])

    t = connect_target(host, port)
    f = open(filename, 'rb')
    
    while True:
        buff = f.read(8192 * 8)
        if not buff:
            break
        t.send(buff)
        
    f.close()
    t.close()