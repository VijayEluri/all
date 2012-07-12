
#import <UIKit/UIKit.h>

@class TaskViewToolbarItemsDelegate;

@interface StartMenuView : UIView <UITableViewDelegate, UITableViewDataSource> {
	UITableView	*tableView;
	TaskViewToolbarItemsDelegate *toolbarItemsDelegate;
	UINavigationController *navigationController;
}

@property (retain) TaskViewToolbarItemsDelegate *toolbarItemsDelegate;
@property (retain) UINavigationController *navigationController;

@end
