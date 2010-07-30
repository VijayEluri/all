require 'mylib'

pr = Prime.new

10000.times { pr.next }
r = pr.next
p r
raise "Error" unless r == 104743