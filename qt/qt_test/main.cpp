#include <QtGui/QApplication>
#include <stdio.h>

#include "mainwindow.h"

#include "version.h"

int main(int argc, char *argv[])
{
    printf("version: %d", VERSION);
    QApplication a(argc, argv);
    MainWindow w;
    w.show();
    return a.exec();
}
