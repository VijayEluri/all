
#import <UIKit/UIKit.h>

@class Tasks;

@interface LogBookViewController : UIViewController <UITableViewDelegate, UITableViewDataSource> {
	Tasks *tasks;
	UITableView	*tableView;
	NSMutableArray *sectionTitleArray;
	NSMutableArray *sectionTasksCountArray;
	NSMutableArray *sectionStartIndexArray;
}

@end
