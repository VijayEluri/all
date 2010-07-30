def recurring_length(num)
  mods = []
  n = 1
  while true
    mod = n % num
    return mods.size - mods.index(mod) if mods.include?(mod)
    mods << mod
    n *= 10
  end
  mods.size
end

value = 0
length = 0
(1..1000).each do |i|
  len = recurring_length i
  value = i and length = len if len > length
end

p value

raise "Error" unless value == 983