
#import <UIKit/UIKit.h>

@class TreeViewTableViewDelegate;
@class TreeViewTableViewDataSource;
@class Task;

@interface TreeViewController : UIViewController {
	UITableView	*tableView;
	UIToolbar *toolbar;
    TreeViewTableViewDelegate *tableViewDelegate;
    TreeViewTableViewDataSource *tableViewDataSource;
    Task *taskToMove;
}

@property (retain) Task *taskToMove;

- (Task *)taskAt:(NSIndexPath *)path;
- (void)openSubTasks:(Task *)task;
- (void)closeSubTasks:(Task *)task;
- (void)moveTaskTo:(Task *)parentTask;

@end
