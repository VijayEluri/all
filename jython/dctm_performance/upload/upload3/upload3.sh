rm -f *src_thread*
rm -f threadlog*.log

export CLASSPATH=.:/dctm/shared/dfc/dfc.jar:$CLASSPATH
/opt/jython2.2.1/jython upload3.py
