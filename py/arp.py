#! /usr/bin/env python
from scapy.all import *

arpt = {}

def arp_monitor_callback(pkt):
    if ARP in pkt and pkt[ARP].op in (2,2): #who-has or is-at
        arp = pkt[ARP]
        if arpt.has_key(arp.psrc):
            if arpt[arp.psrc] != arp.hwsrc:
                print "ERROR!: %s was %s, now %s" % (arp.psrc, arpt[arp.psrc], arp.hwsrc)
        else:
            arpt[arp.psrc] = arp.hwsrc
            print "INFO: %s is %s" % (arp.psrc, arp.hwsrc)

sniff(prn=arp_monitor_callback, filter="arp", store=0)