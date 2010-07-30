A = 1
B = 2
C = 5
D = 10
E = 20
F = 50
G = 100
H = 200

count = 0

0.step(H, A).each do |a|
  lefta = H - a
  0.step(lefta, B).each do |b|
    leftb = lefta - b
    0.step(leftb, C).each do |c|
      leftc = leftb - c
      0.step(leftc, D).each do |d|
        leftd = leftc - d
        0.step(leftd, E).each do |e|
          lefte = leftd - e
          0.step(lefte, F).each do |f|
            leftf = lefte - f
            0.step(leftf, G).each do |g|
              leftg = leftf - g
              0.step(leftg, H).each do |h|
                count += 1 if a + b + c + d + e + f + g + h == 200
              end
            end
          end
        end
      end
    end
  end
end

p count

raise "Error" unless count == 73682