def base_num(d):
    if d == 0:
        return 0
    return 10 ** d

def d(x):
    if x < 10:
        x -= 1
    digits = 0
    nextcount = 0
    count = 0
    while True:
        count = nextcount
        digits += 1
        nextcount = count + digits * (base_num(digits) - base_num(digits - 1))
        if x >= count and x < nextcount:
            index = x - count
            num = 10 ** (digits - 1) + index / digits
            return int(str(num)[index % digits])

indexes = [1, 10, 100, 1000, 10000, 100000, 1000000]

result = 1
for i in indexes:
    result *= d(i)
    
print result