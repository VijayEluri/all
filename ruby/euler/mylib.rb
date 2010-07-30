class Prime

  def initialize
    reset
  end
  
  def reset
    @prime_index = 0
  end
  
  def next
    @prime_cache ||= [2, 3, 5]
    if @prime_index < @prime_cache.size
      r = @prime_cache[@prime_index]
      @prime_index += 1 
      return r
    end

    num = @prime_cache[-1] + 2
    while true
      # if not multiple_of_prime? num
      if is_with_cache? num
          @prime_cache << num
          @prime_index += 1
          return num
        # end
      end
      num += 2
    end
  end
    
  def first_factor(num)
    oldindex = @prime_index
    reset
    i = self.next
    while i < num
      if num % i == 0
        break
      end
      i = self.next
    end
    @prime_index = oldindex
    i
  end
  
  def sum_of_divisors(num)
    # print "-- #{num}\n"
    @div_cache ||= {}
    return 1 if num == 1
    return @div_cache[num] unless @div_cache[num].nil?
    
    start = 2 #self.first_factor(num)
    return 1 if start == num
    stop = num / start
        
    div = [1]
    
    i = start
    while i < stop
      if num % i == 0
        div << i
        stop = num / i
        div << stop unless stop == i
      end
      i += 1
    end

    # p div
    sum = div.inject{ |r, n| r + n }
    @div_cache[num] = sum
    sum
  end

  private

  def is_with_cache?(num)
    sqrt = Math.sqrt(num).ceil
    @prime_cache.each do |i|
      return true if i > sqrt
      return false if num % i == 0
    end
    true
  end

  def multiple_of_prime?(num)
    @prime_cache.each do |i|
      if num % i == 0
        return true
      end
    end
    false
  end
  
end

class Integer
  @@prime = Prime.new
  def is_prime?
    return false if self == 1
    return true if self == 2
    (2..Math.sqrt(self).ceil).each { |i| return false if self % i == 0 }
    true
  end
  
  def prime_factors
    @@prime.reset
    
    factors = []

    i = @@prime.next
    num = self
    while i < num
      if num % i == 0
        num = num / i
        factors << i
        @@prime.reset
      end
      i = @@prime.next
    end
    factors << num # current num cannot be evenly divided by any prime number less than it, itself must be a prime
  end
  
  def prime_factors_in_hash
    r  = {}
    self.prime_factors.each do |v|
      r[v] ||= 0
      r[v] += 1
    end
    r
  end
  
  def factorial
    (2..self).inject(1) { |r, n| r * n }
  end

  def palindromic?
    str = self.to_s
    str.palindromic?
  end
  
  def pandigital?
    str = self.to_s
    len = str.size
    return false if str.chars.to_a.uniq.size < len
    return false if str.include?('0')
    len.times { |i| return false if str[i] > len.to_s[0] }
    true
  end

  def binary_s
    [self].pack("N").unpack("B32")[0].sub(/^0+(?=\d)/, '')
  end
  
end

class String
  def palindromic?
    len = self.size
    len.times { |i| return false if self[i] != self[len - i - 1] }
    true
  end
end
