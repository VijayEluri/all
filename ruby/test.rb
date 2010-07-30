#!/usr/bin/env ruby

#array
v1 = [1, "2", ["3", 4]]

#hash
v2= {"key1" => 1, "key2" => "2"}

#const
C1 = "Hello"

class C < Object
  p(self.class())
   Const = "in C" 
 
   p(Const)
 
   def myupcase(str)
     p(self)
      return str.upcase()
  end
end

p(C.new().myupcase("content"))


v1.each do |i|
  p i
end

v2.each { |v| v.each { |k| puts k } }

(1..5).each do |i|
  print i
end

p "Ruby finally has a killer app. It's Ruby on Rails.".capitalize

p "yes" if "color" =~ /colou?r/

[[1, 2], [3, 4]].each do |x, y|
  print x, y, "\n"
end

v2.each do |x, y|
  p "key #{x}, value #{y}"
end