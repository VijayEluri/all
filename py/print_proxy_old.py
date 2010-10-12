import os
import sys
import signal
import socket
import select
import logging
import thread
from threading import Thread
from datetime import datetime

class ConnectionHandler:
    def __init__(self, connection, address, timeout, target_host, target_port):
        
        self.client_socket = connection
        self.local_address = address
        self.timeout = timeout
        self.target_host = target_host
        self.target_port = target_port
        self.logger = logging.getLogger('ConnectionHandler')
        self.assign_target_socket()
        self.create_folder_and_file()

        self.read_write()

    def assign_target_socket(self):
        try:
            self.target_socket = self.connect_target(target_host, target_port)
        except:
            self.target_socket = 0
            
    def create_folder_and_file(self):
        (folder, filename) = self.get_file_name()
        try:
            os.mkdir(folder)
        except:
            pass
        self.file = open(os.path.join('.', folder, filename), 'wb')
        
        
    def connect_target(self, host, port):
        (soc_family, _, _, _, address) = socket.getaddrinfo(host, port)[0]
        target = socket.socket(soc_family)
        target.connect(address)
        self.logger.info("connected to target %s:%d" % (host, port))
        
        return target

    def read_write(self):
        time_out_max = self.timeout / 3

        if self.target_socket:
            socs = [self.client_socket, self.target_socket]
        else:
            socs = [self.client_socket]
            
        count = 0

        while 1:
            count += 1
            (recv, _, error) = select.select(socs, [], socs, 3)

            if error:
                self.logger.info("client disconnected")
                break
                
            if recv:
                for in_ in recv:
                    data = in_.recv(8192 * 4)
                    if in_ is self.client_socket:
                        out = self.target_socket
                    else:
                        out = self.client_socket
                        
                    if data:
                        count = 0
                        self.file.write(data)
                        if out:
                            out.send(data)

            if count == time_out_max:
                (host, port) = self.client_socket.getpeername()
                self.logger.info("client %s:%d timeout" % (host, port))
                break
                
        if self.file:
            self.file.close()
                
    def dump_data(self, src, length = 16):
        if not self.logger.isEnabledFor(logging.DEBUG):
            return
        digits = 4 if isinstance(src, unicode) else 2
        for i in xrange(0, len(src), length):
            s = src[i : i + length]
            hexa = b' '.join(["%0*X" % (digits, ord(x))  for x in s])
            text = b''.join([x if 0x20 <= ord(x) < 0x7F else b'.'  for x in s])
            self.logger.debug(b"%04X   %-*s   %s" % (i, length*(digits + 1), hexa, text))
            
    def get_file_name(self):
        (host, port) = self.client_socket.getpeername()
        now = datetime.now().strftime('%Y%m%d.%H%M%S.%f.pcl')
        self.logger.info("generated dir name %s, file name %s" % (host, now))
        return (host, now)        

    def analyze_data(self, data):
        self.get_file_name()
        pass
        
def start_server(host, port, t_host, t_port):
    logger = logging.getLogger('start_server')
    logger.info("starting server %s:%d" % (host, port))
        
    soc = socket.socket(socket.AF_INET)
    soc.bind((host, port))
    soc.listen(1)
    
    def signal_handler(signal, frame):
        print
        logger.info("stopping server.")
        soc.close()
        sys.exit(1)
        
    signal.signal(signal.SIGINT, signal_handler)
    
    while 1:
        thread.start_new_thread(ConnectionHandler, soc.accept() + (60, t_host, t_port))
        
def usage():
    print "Usage: %s <local ip> <listen port> <target ip> <target port>" % sys.argv[0]
    exit(1)

if __name__ == '__main__':
    if (len(sys.argv) < 5):
        usage()
    host = sys.argv[1]
    port = int(sys.argv[2])
    target_host = sys.argv[3]
    target_port = int(sys.argv[4])

    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)-8s %(name)s  %(message)s')

    start_server(host, port, target_host, target_port)        