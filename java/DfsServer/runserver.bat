@echo off

set JAVA_HOME=d:\Java\jdk1.5.0_15\
set CLASSPATH=.\bin\;.\config\;


FOR /F "tokens=*" %%f IN ('dir /b lib\*.jar') DO call cpappend.bat lib\%%f
FOR /F "tokens=*" %%f IN ('dir /b dfslib\*.jar') DO call cpappend.bat dfslib\%%f

%JAVA_HOME%\bin\java com.dfs.server.Main

