import os
import sys
import time
import select
import socket
import hashlib
import logging
from datetime import datetime
from struct import *
import time,ctypes

"""
Frame format:
--------------------------------
| hash | packet id | timestamp |
--------------------------------
| data                         |
--------------------------------
"""

DATA_SIZE = 1024
HASH_SIZE = hashlib.md5().digest_size
WHOLE_FRAME_SIZE = DATA_SIZE + HASH_SIZE + 8 + 8
RECV_BUF_SIZE = WHOLE_FRAME_SIZE

def usage():
    print "Usage: %s [server|client] <IP address> <port> <microseconds between send(client only)>" % sys.argv[0]
    exit(1)
    
def get_freq():
    freq = ctypes.c_longlong(0)
    ctypes.windll.kernel32.QueryPerformanceFrequency(ctypes.byref(freq))
    return freq.value    

def get_counter():
    value = ctypes.c_longlong(0)
    ctypes.windll.kernel32.QueryPerformanceCounter(ctypes.byref(value))
    return value.value

def usleep(us):
    time.sleep(us / 1000.0)
    #s = get_counter()
    #f = get_freq() + 0.0
    #while((get_counter() - s) / f) < (us / 1000000.0):
    #    time.sleep(0)

def sizeof_fmt(num):
    for x in ['B','KB','MB','GB','TB']:
        if num < 1024.0:
            return "%3.1f%s" % (num, x)
        num /= 1024.0
    
def verify_whole_frame(whole_frame):
    m = hashlib.md5()
    hash = whole_frame[:HASH_SIZE]
    packetid = unpack('q', whole_frame[HASH_SIZE:HASH_SIZE + 8])[0]
    data = whole_frame[HASH_SIZE + 16:]
    m.update(data)
    if (m.digest() == hash):
        return (0, packetid)
    else:
        return (1, packetid)

def calc_latency(frame):
    timestamp_str = frame[HASH_SIZE + 8:HASH_SIZE + 16]
    time1 = unpack('q', timestamp_str)[0]
    time2 = get_counter()
    return (time2 - time1) * 1000 / get_freq()

def server(host, port):
    logger = logging.getLogger('server')
    line_fmt = "%10s%10s%10s%10s%10s%10s%10s%10s%9s%10s"
    soc = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    soc.bind((host, port))
    soc_back = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    whole_frame = ''
    start_time = datetime.now()
    failed_frame = 0
    total_frame = 0
    last_5_sec_frame = 0
    last_5_sec_failed = 0
    print_count = 0
    lost_frame = 0
    first_packet_id = 0
    
    while(1):
        (data, addr) = soc.recvfrom(RECV_BUF_SIZE)
        whole_frame = whole_frame + data
        if len(whole_frame) >= WHOLE_FRAME_SIZE:
            frame = whole_frame[:WHOLE_FRAME_SIZE]
            (failed, packetid) = verify_whole_frame(frame)
            frame[HASH_SIZE:HASH_SIZE + 8] = pack('q', total_frame + 1)
            soc_back.sendto(frame, (addr[0], port + 1))
            if first_packet_id == 0:
                first_packet_id = packetid
            failed_frame += failed
            last_5_sec_failed += failed
            total_frame += 1
            last_5_sec_frame += 1
            lost_frame = packetid - first_packet_id - total_frame + 1
            whole_frame = whole_frame[WHOLE_FRAME_SIZE:]

        diff = datetime.now() - start_time
        if (diff.seconds > 0 and diff.seconds % 5 == 0):
            if need_print:
                if (print_count % 20 == 0):
                    logger.info("")
                    logger.info(line_fmt % ('SECONDS', 'BYTES', 'PACKETS', 'FAILS', '5PACKETS', '5FAILS', 'BPS', '5BPS', 'LOST', 'LOST%'))

                total_bytes = total_frame * WHOLE_FRAME_SIZE
                last_5_sec_bytes = last_5_sec_frame * WHOLE_FRAME_SIZE
                total_diff = diff.seconds + diff.microseconds  / 1000000.0
                overall_speed = sizeof_fmt(total_bytes / total_diff)
                last_5_sec_speed = sizeof_fmt(last_5_sec_bytes / 5.0)
                
                logger.info(line_fmt % 
                    (diff.seconds, sizeof_fmt(total_bytes), total_frame, 
                    failed_frame, last_5_sec_frame, last_5_sec_failed, 
                    overall_speed, last_5_sec_speed, lost_frame, "%.3f%s" % (lost_frame * 100 / (total_frame + 0.0), '%')))
                print_count += 1
                need_print = False
                last_5_sec_frame = 0
                last_5_sec_failed = 0
        else:
            need_print = True
        
def client_generate_random_string(packet_id):
    random_string = os.urandom(DATA_SIZE)
    m = hashlib.md5()
    m.update(random_string)
    timestamp = get_counter()
    
    return m.digest() + pack('q', packet_id) + pack('q', timestamp) + random_string
    

def client(host, port, ms):
    logger = logging.getLogger('client')
    line_fmt = "%10s%10s%10s%10s%10s%10s%10s%10s"

    soc = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    soc.bind(('0.0.0.0', port + 1))
    target = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    print "connected to target %s:%d" % (host, port)
    print "sending data..."
    start_time = datetime.now()
    total_size = 0
    total_count = 0
    print_count = 0
    first_packet_id = 0
    failed_frame = 0
    latency_in_5_sec = 0
    received_in_5_count = 0
    received = 0
    lost = 0
    while(1):
        data = client_generate_random_string(total_count + 1)
        target.sendto(data, (host, port))
        (recv, _, _) = select.select([soc], [], [], 0)
        if recv:
            frame = soc.recv(RECV_BUF_SIZE)
            (failed, packetid) = verify_whole_frame(frame)
            latency_in_5_sec += calc_latency(frame)
            if first_packet_id == 0:
                first_packet_id = packetid
            failed_frame += failed
            received_in_5_count += 1
            received += 1
            lost = packetid - received + 1
        usleep(ms)
        total_size += len(data)
        total_count += 1
        diff = datetime.now() - start_time
        if (diff.seconds > 0 and diff.seconds % 5 == 0):
            if need_print:
                if (print_count % 20 == 0):
                    logger.info("--------")
                    logger.info(line_fmt % ('BYTES', 'PACKETS', 'SECONDS', 'BPS', 'PPS', "RECEIVED", "LOST", "LATENCY"))
                latency = 0
                if received_in_5_count > 0:
                    latency = latency_in_5_sec / received_in_5_count
                logger.info(line_fmt % 
                    (sizeof_fmt(total_size), total_count, diff.seconds, sizeof_fmt(total_size / diff.seconds), 
                    sizeof_fmt(total_count / diff.seconds), received, lost, ("%dms" % (latency))))
                print_count += 1
                need_print = False
                latency_in_5_sec = 0
                received_in_5_count = 0
        else:
            need_print = True
    
def setup_logging(logfile):
    logging.basicConfig(filename=logfile, level=logging.DEBUG, format='%(asctime)s - %(levelname)-8s %(name)s  %(message)s')
    console = logging.StreamHandler()
    console.setLevel(logging.DEBUG)
    logging.getLogger('').addHandler(console)
    
def main(is_server, host, port):
    
    if is_server:
        if (len(sys.argv) != 4):
            usage()
        print "Running as a server on %s:%d" % (host, port)
        setup_logging("server.log")
        server(host, port)
    else:
        if (len(sys.argv) != 5):
            usage()
        print "Running as client connect to %s:%d" % (host, port)
        setup_logging("client.log")
        ms = int(sys.argv[4])
        client(host, port, ms)


if __name__ == '__main__':
    if (len(sys.argv) < 4):
        usage()

    is_server = True
    
    if sys.argv[1] == 'client':
        is_server = False
    
    host = sys.argv[2]
    port = int(sys.argv[3])

    main(is_server, host, port)