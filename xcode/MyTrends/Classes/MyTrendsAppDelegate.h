
#import <UIKit/UIKit.h>
#import "MainViewController.h"

@interface MyTrendsAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    MainViewController* m_mainViewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;

@end

