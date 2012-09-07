#!/usr/bin/ruby


print "Network Interfaces\n------------------\n"
none = true

input = %x{networksetup -listallnetworkservices}.split("\n")

input.each do |service|
  infos = %x{networksetup -getinfo "#{service}"}.split("\n")
  ipaddr = mask = router = nil
  infos.each do |line|
    ipaddr = $1 if line =~ /^IP address: (.*)/
    mask = $1 if line =~ /^Subnet mask: (.*)/
    router = $1 if line =~ /^Router: (.*)/
  end
  if ipaddr
    puts "#{service}:#{' ' * (9 - service.length)}#{ipaddr} mask #{mask} gateway #{router}"
    none = false
  end
end

if none
  puts "No network connection"
end