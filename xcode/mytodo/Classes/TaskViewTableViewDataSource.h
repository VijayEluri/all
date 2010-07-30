
#import <Foundation/Foundation.h>
#import "TaskViewGeneralDelegate.h"

@class Tasks;
@class Task;

@interface UpdateTaskResult : NSObject
{
	int insertPosition;
	int oldSection;
	int newSection;
}
@property int insertPosition;
@property int oldSection;
@property int newSection;

@end


@interface TaskViewTableViewDataSource : TaskViewGeneralDelegate <UITableViewDataSource> {
	NSMutableArray *dataArray;
	NSString *nowDate;
}

- (UpdateTaskResult *)allocUpdateDataResultByTask:(Task *)task;
- (void)addTask:(Task *)task;
- (void)sortTasks;
- (int)openTasksCount;
- (Tasks *)tasksAtSection:(int)section;
- (void)loadTasks;

@end
