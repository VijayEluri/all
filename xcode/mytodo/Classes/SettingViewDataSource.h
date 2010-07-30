
#import <Foundation/Foundation.h>

@class SettingViewController;

@interface SettingViewDataSource : NSObject <UITableViewDataSource> {
	SettingViewController *settingViewController;
}

@property (assign) SettingViewController *settingViewController;

@end
