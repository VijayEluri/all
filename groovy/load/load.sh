cp=.

for jar in $DOCUMENTUM_SHARED/dfc/*.jar
do
  cp=$cp:$jar
done

for jar in lib/*.jar
do
  cp=$cp:$jar
done

export CLASSPATH=$cp
export JAVA_OPTS=-Xmx1024m


groovy loadabc.groovy $@
