javac -cp ".:lib/*" src/gltest/*.java
mkdir build
# copy files into build directory
copy -r "src/*.class" "build/" 
# delete built files
rm src/gltest/.class