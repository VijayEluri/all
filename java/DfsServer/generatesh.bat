@echo off

echo JAVA_HOME= > runserver.sh

set CLASSPATH=./bin/:./config/


FOR /F "tokens=*" %%f IN ('dir /b lib\*.jar') DO call cpappend_sh.bat lib/%%f
FOR /F "tokens=*" %%f IN ('dir /b dfslib\*.jar') DO call cpappend_sh.bat dfslib/%%f

echo cp=%CLASSPATH% >> runserver.sh
echo $JAVA_HOME/bin/java -cp $cp com.dfs.server.Main >> runserver.sh

