
#import <UIKit/UIKit.h>


@class TodayViewTableViewDelegate;
@class TodayViewTableViewDataSource;
@class TodayViewCell;
@class Task;

@interface TodayViewController : UIViewController {
	TodayViewTableViewDelegate *tableViewDelegate;
	TodayViewTableViewDataSource *tableViewDataSource;
	UITableView	*tableView;
}

- (Task *)taskAt:(NSIndexPath *)path;
- (void)removeCell:(TodayViewCell *)cell;

@end
