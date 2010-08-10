set JAVA_EXE="c:\Java\jdk1.4.2_14\bin\java.exe"
set JAVA_ARGS=-Xms512m -Xmx512m -XX:PermSize=256m
set CP=.;c:\Progra~1\Documentum\dctm.jar;c:\Documentum\config\
%JAVA_EXE% %JAVA_ARGS% -cp %CP% Test3
