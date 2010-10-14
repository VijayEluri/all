'''
generate the bridge.sh, proxy.sh, filter.sh
'''

bridge_sh = '''
ip addr flush eth0
ip addr flush eth1
brctl delbr br0
brctl addbr br0
brctl addif br0 eth0
brctl addif br0 eth1
ifconfig br0 %s netmask %s
route add default gw %s dev br0
'''

proxy_sh = '''
python print_proxy.py %s %d %s %d
'''

import os

def create_file(filename, content):
    f = open(filename, 'w')
    f.write(content)
    f.close()

def main():
    ip = raw_input('IP address -> ')
    netmask = raw_input('Net mask -> ')
    gateway = raw_input('Gateway -> ')
    local_port = raw_input('Listen port (default 9100) -> ')
    target_ip = raw_input('Printer IP address -> ')
    target_port = raw_input('Printer port (default 9100) -> ')
    
    try:
        local_port = int(local_port)
    except:
        local_port = 9100
        
    try:
        target_port = int(target_port)
    except:
        target_port = 9100
        
    print "Local IP: %s, mask: %s, gateway: %s" % (ip, netmask, gateway)
    print "Proxy %s:%d -> %s:%d" % (ip, local_port, target_ip, target_port)
    
    create_file('bridge.sh', bridge_sh % (ip, netmask, gateway))
    create_file('proxy.sh', proxy_sh % (ip, local_port, target_ip, target_port))
    
    os.system('chmod +x bridge.sh')
    os.system('chmod +x proxy.sh')
    
    pass

if __name__ == '__main__':
    main()