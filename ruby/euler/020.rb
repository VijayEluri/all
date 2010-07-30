require 'mylib'

r = 100.factorial.to_s.chars.inject(0) { |sum, n| sum + n.to_i }
p r
raise "Error" unless r == 648