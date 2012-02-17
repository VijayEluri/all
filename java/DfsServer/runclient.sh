cp=./bin:./config

for jar in `find ./lib ./dfslib -name "*.jar"`
do
	cp=$cp:$jar
done

java -cp $cp com.dfs.client.Main
