cp=.
for jar in /Users/hu/src/DFC6.0/*
do
  cp=$cp:$jar
done

export CLASSPATH=$cp
export JAVA_OPTS=-Xmx1024m


groovy dctm.groovy
