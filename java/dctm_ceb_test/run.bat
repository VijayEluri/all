set JAVA_EXE="c:\Java\jdk1.4.2_14\bin\java.exe"
set JAVA_ARGS=-Xms1024m -Xmx1024m -DDebugSessionManager
set CP=.;c:\Progra~1\Documentum\dctm.jar;c:\Documentum\config\
%JAVA_EXE% %JAVA_ARGS% -cp %CP% Test3
