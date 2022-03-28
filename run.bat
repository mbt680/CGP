IF "%1"=="" GOTO HAVE_0
IF "%2"=="" GOTO HAVE_1
IF "%3"=="" GOTO HAVE_2

:HAVE_0
:HAVE_1:
java -cp ";lib/*;build/" gltest.ModelViewer
exit

:HAVE_2:
java -cp ";lib/*;build/" gltest.ModelViewer %1 %2

