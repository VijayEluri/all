
#import <UIKit/UIKit.h>

@class TaskViewToolbarItemsDelegate;

@interface StartMenuView : UIView <UITableViewDelegate, UITableViewDataSource> {
	UITableView	*tableView;
	TaskViewToolbarItemsDelegate *toolbarItemsDelegate;
	UINavigationController *navigationController;
}

@property (assign) TaskViewToolbarItemsDelegate *toolbarItemsDelegate;
@property (assign) UINavigationController *navigationController;

@end
