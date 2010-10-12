while true 
do
clear
ls -l 10.240.4.241/ | awk '{print $5}' | sort | uniq -c
sleep 2
done
