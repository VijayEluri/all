def pandigital?(num)
  str = ""
  i = 1
  while true
    str << (num * i).to_s
    break if str.size == 9
    return false if str.size > 9
    i += 1
  end
  return false if str.include?('0')
  return false if str.chars.to_a.uniq.size < 9
  print "#{num}, #{i}, #{str}\n"
  true
end

(1..876).each do |x|
  num = ('9' + x.to_s).to_i
  pandigital?(num)
end
