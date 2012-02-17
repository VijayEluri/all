FILE="/System/Library/Extensions/IOAHCIFamily.kext/Contents/PlugIns/IOAHCIBlockStorage.kext/Contents/MacOS/IOAHCIBlockStorage"
if [ -e $FILE.original ]
then
	sudo cp $FILE.original $FILE
	echo "restored"
fi
