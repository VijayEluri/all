cp=.
for jar in lib/*
do
  cp=$cp:$jar
done

export CLASSPATH=$cp
export JAVA_OPTS=-Xmx1024m


groovy Upload.groovy $@
