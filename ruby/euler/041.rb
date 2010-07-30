require 'mylib'

p = Prime.new

while true
  n = p.next
  p n if n.pandigital?
end