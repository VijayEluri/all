# encoding:UTF-8

depts = ['台', '政', '后']
ppls = ['B', 'S']

depts.each do |d|
  ppls.each do |p|
    p "A: 台政"
    p "#{p}: #{d}"
  end
end