
#import <UIKit/UIKit.h>

@class SettingViewDelegate;
@class SettingViewDataSource;

@interface SettingViewController : UIViewController {
	UITableView	*tableView;
	SettingViewDelegate *tableViewDelegate;
	SettingViewDataSource *tableViewDataSource;
	UISwitch *animationSwitch;
}

@property (readonly) UISwitch *animationSwitch;

@end
