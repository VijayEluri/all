sum1, sum2 = 0, 0
1.step(100, 1) { |v| sum1 += v * v; sum2 += v }

p sum2 ** 2 - sum1

