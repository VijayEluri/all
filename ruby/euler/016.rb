(2 ** 1000).to_s.chars { |c| @sum ||= 0 and @sum += c.to_i }
p @sum

raise "Error" unless @sum == 1366