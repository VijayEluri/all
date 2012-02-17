FILE="/System/Library/Extensions/IOAHCIFamily.kext/Contents/PlugIns/IOAHCIBlockStorage.kext/Contents/MacOS/IOAHCIBlockStorage"
if [ -e $FILE ]
then
	echo "Already backed up"
	exit 1
fi
sudo cp $FILE $FILE.original
