
#import <Foundation/Foundation.h>

@interface AppSession : NSObject {
	UINavigationController *navigationController;
}

- (AppSession *)init:(UINavigationController *)controller;
- (void)saveState;
- (void)restoreState;

@end
