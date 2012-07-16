sudo launchctl unload -w /System/Library/LaunchDaemons/com.apple.InternetSharing.plist
sleep 10
sudo launchctl load -w /System/Library/LaunchDaemons/com.apple.InternetSharing.plist
