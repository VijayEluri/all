
ip addr flush eth0
ip addr flush eth1
brctl delbr br0
brctl addbr br0
brctl addif br0 eth0
brctl addif br0 eth1
ifconfig br0  netmask 
route add default gw  dev br0
