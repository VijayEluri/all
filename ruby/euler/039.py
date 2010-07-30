def find_solutions(num):
    count = 0
    for i in xrange(1, num / 3):
        for j in xrange(i, (num - i) / 2):
            k = num - i - j
            if k < i or k < j:
                continue
            if k * k == i * i + j * j:
                count += 1
    return count

maxcount = 0
maxnum = 0

for x in range(1, 1000):
    c = find_solutions(x)
    if c > maxcount:
        maxcount = c
        maxnum = x

print maxcount, maxnum
