if [$# == 0]
then
java -cp ";lib/*;build/" gltest.ModelViewer $1 $2
else
java -cp ";lib/*;build/" gltest.ModelViewer
fi