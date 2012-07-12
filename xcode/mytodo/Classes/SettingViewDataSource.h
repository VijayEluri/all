
#import <Foundation/Foundation.h>

@class SettingViewController;

@interface SettingViewDataSource : NSObject <UITableViewDataSource> {
	SettingViewController *settingViewController;
}

@property (retain) SettingViewController *settingViewController;

@end
