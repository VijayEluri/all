1000.times { |i| @sum ||= 0 and @sum += i if i % 3 == 0 or i % 5 == 0 }

print @sum

raise "Error" if @sum != 233168