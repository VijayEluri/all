import sets

magic_chars = sets.Set('abracadabra')
poppins_chars = sets.Set('supercalifragilisticexpialidocious')
print ''.join(magic_chars & poppins_chars)   # set intersection

print map(chr, range(90, 100))
print [chr(c) for c in range(90, 100)]
print map(ord, 'abcdefghijklmn')

str1 = ''.join(map(chr, range(90, 100)))
str2 = str1[::-1]

print str1
print str2

def test():
    "Stupid test function"
    L = []
    for i in range(100):
        L.append(i)

if __name__=='__main__':
    from timeit import Timer
    t = Timer("test()", "from __main__ import test")
    print t.timeit(number=100000)
