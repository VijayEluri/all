cp=./bin:./config

for jar in `find ../jars/dfs6.5/ ./lib -name "*.jar"`
do
	cp=$cp:$jar
done

echo java -cp $cp com.dfs.server.Main
java -cp $cp com.dfs.server.Main
