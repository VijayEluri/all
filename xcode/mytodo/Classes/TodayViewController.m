
#import "TodayViewController.h"
#import "TodayViewTableViewDelegate.h"
#import "TodayViewTableViewDataSource.h"
#import "TodayViewCell.h"

@implementation TodayViewController

- (void)loadView {
	tableViewDelegate = [[TodayViewTableViewDelegate alloc] init];
	tableViewDelegate.todayViewController = self;
	
	tableViewDataSource = [[TodayViewTableViewDataSource alloc] init];
	tableViewDataSource.todayViewController = self;
	
	tableView = [[UITableView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] 
											 style:UITableViewStylePlain];	
	tableView.delegate = tableViewDelegate;
	tableView.dataSource = tableViewDataSource;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	
	self.view = tableView;
}

- (Task *)taskAt:(NSIndexPath *)path {
	return [tableViewDataSource taskAt:path];
}

- (void)removeCell:(TodayViewCell *)cell {
	int sectionToRemove = [tableViewDataSource removeTask:cell.task];
	NSIndexPath *indexPath = [tableView indexPathForCell:cell];
	if (sectionToRemove >= 0) {
		[tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionToRemove] withRowAnimation:UITableViewRowAnimationFade];
	} else {
		[tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
	}
}

@end
