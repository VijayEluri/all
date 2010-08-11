set -e

echo
echo "start building"
echo

./version.sh
echo "version updated"
echo

qmake -Wall qt_test.pro CONFIG+=qt_test
echo "qmake ok for qt_test."
make -s clean
echo "qt_test cleaned."
make -s
echo "qt_test made."
echo
echo "----"
echo
qmake qt_test.pro CONFIG+=hello
echo "qmake ok for hello."
make -s clean
echo "hello cleaned."
make -s
echo "hello made."
echo
echo "----all done."
