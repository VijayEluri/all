class Collatz
  def next(num)
    num % 2 == 0 ? num / 2 : 3 * num + 1
  end

  def count(num)
    @cache ||= {}
    n = num
    c = 0
    need_to_be_cached = {}
    while n != 1
      if not @cache[n].nil?
        c += @cache[n] - 1
        break
      end
      c += 1
      n = self.next(n)
      need_to_be_cached[n] = c
    end
    need_to_be_cached.each { |k, v| @cache[k] = c + 1 - v  }
    @cache[num] = c + 1
  end
end

coll = Collatz.new

max = 0
num = 0
(1..1000000).each do |i|
  c = coll.count i
  if c > max
    num = i
    max = c
  end
end

p num, max

raise "Error" unless num == 837799