javac -cp ".;lib/*" src/gltest/*.java
md build
:: copy files into build directory
xcopy /y /e "src\*.class" "build\" 
:: delete built files
del src\gltest\*.class