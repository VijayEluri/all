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
    def __init__(self, connection, address, timeout, target_host, target_port, server_only):
        
        self.client_socket = connection
        self.local_address = address
        self.timeout = timeout
        self.target_host = target_host
        self.target_port = target_port
        self.server_only = server_only
        self.logger = logging.getLogger('ConnectionHandler')
        
        if not server_only:
            self.target_socket = self.connect_target(target_host, target_port)
        
        self.read_write()

    def connect_target(self, host, port):
        (soc_family, _, _, _, address) = socket.getaddrinfo(host, port)[0]
        target = socket.socket(soc_family)
        target.connect(address)
        self.logger.info("connected to target %s:%d" % (host, port))
        
        return target

    def read_write(self):
        time_out_max = self.timeout / 3

        if server_only:
            socs = [self.client_socket]
        else:
            socs = [self.client_socket, self.target_socket]
            
        count = 0

        while 1:
            count += 1
            (recv, _, error) = select.select(socs, [], socs, 3)

            if error:
                break
            if recv:
                for in_ in recv:
                    data = in_.recv(8192)
                    if in_ is self.client_socket:
                        if not server_only:
                            out = self.target_socket
                    else:
                        out = self.client_socket
                    if data:
                        self.analyze_data(data)
                        
                        if server_only:
                            self.dump_data(data)
                        else:
                            out.send(data)
                        count = 0

            if count == time_out_max:
                break
                
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
        self.logger.debug("generated dir name %s, file name %s" % (host, now))
        return (host, now)        

    def analyze_data(self, data):
        self.get_file_name()
        pass
        
def start_server(host, port, t_host, t_port, server_only):
    logger = logging.getLogger('start_server')
    if server_only:
        logger.info("starting server %s:%d" % (host, port))
    else:
        logger.info("starting proxy server %s:%d" % (host, port))
        
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
        thread.start_new_thread(ConnectionHandler, soc.accept() + (60, t_host, t_port, server_only))
        
def usage():
    print "Usage: %s <local ip> <listen port> <target ip> <target port> [end]" % sys.argv[0]
    exit(1)

if __name__ == '__main__':
    if (len(sys.argv) < 5):
        usage()
    host = sys.argv[1]
    port = int(sys.argv[2])
    target_host = sys.argv[3]
    target_port = int(sys.argv[4])

    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)-8s %(name)s  %(message)s')

    server_only = False
    if (len(sys.argv) == 6):
        server_only = (sys.argv[5] == 'end')

    start_server(host, port, target_host, target_port, server_only)        