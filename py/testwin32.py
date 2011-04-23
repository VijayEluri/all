import time,ctypes

freq = ctypes.c_longlong(0)
ctypes.windll.kernel32.QueryPerformanceFrequency(ctypes.byref(freq))
f=freq.value

print f

ctypes.windll.kernel32.QueryPerformanceCounter(ctypes.byref(freq))
n1=freq.value
print n1

time.sleep(1)           
ctypes.windll.kernel32.QueryPerformanceCounter(ctypes.byref(freq))
n2=freq.value
print n2

print (n2-n1)/(f+0.)