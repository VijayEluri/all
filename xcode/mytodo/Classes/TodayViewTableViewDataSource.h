
#import <Foundation/Foundation.h>

@class Task;
@class Tasks;
@class TodayViewController;

@interface TodayViewTableViewDataSource : NSObject <UITableViewDataSource> {
	Tasks *tasks;
	TodayViewController *todayViewController;
	NSMutableArray *sectionTitleArray;
	NSMutableArray *sectionTasksCountArray;
	NSMutableArray *sectionStartIndexArray;
}

@property (retain) TodayViewController *todayViewController;

- (Task *)taskAt:(NSIndexPath *)path;
- (int)removeTask:(Task *)task;
- (void)reloadData;

@end
