
#import <UIKit/UIKit.h>
#import "DbManager.h"

@class TaskViewController;
@class TaskViewCell;
@class AppSession;

@interface AppDelegate : NSObject <UIApplicationDelegate> {
    AppSession *session;
    UIWindow *window;
    UINavigationController *navigationController;
	TaskViewController *rootTaskViewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *navigationController;

@end

