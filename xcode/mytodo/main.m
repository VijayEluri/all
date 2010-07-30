
#import <UIKit/UIKit.h>

int main(int argc, char *argv[]) {
    NSLog(@"-- start -- very entry");
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
    NSLog(@"-- start -- calling UIApplicationMain");
    int retVal = UIApplicationMain(argc, argv, nil, nil);
    [pool release];
    return retVal;
}
