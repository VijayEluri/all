
#import <Foundation/Foundation.h>

@interface TaskData : NSObject {
	int taskId;
	int parentId;
    int categoryId;
	int priority;
	int completed;
	NSString *content;
	NSString *completionDate;
	NSString *dueDate;
	int subTasksCount;
	int completedSubTasksCount;

	BOOL hasLoaded;
	BOOL hasLoadedSubTasksCount;
	BOOL hasLoadedCompletedSubTasksCount;
}

@property int taskId;
@property int parentId;
@property int categoryId;
@property int priority;
@property int completed;
@property (retain) NSString *content;
@property (retain) NSString *completionDate;
@property (retain) NSString *dueDate;
@property int subTasksCount;
@property int completedSubTasksCount;

@end

@interface Task : NSObject <UIAlertViewDelegate> {
	TaskData *data;
	Task *parentTask;
	
    BOOL open;
}

@property BOOL open;

- (Task *)init;
- (Task *)initWithId:(int)value;

- (int)subTasksCount;

- (int)completedSubTasksCount;

- (int)taskId;
- (void)setTaskId:(int)value;

- (int)parentId;
- (void)setParentId:(int)value;

- (int)categoryId;
- (void)setCategoryId:(int)value;

- (int)priority;
- (void)setPriority:(int)value;

- (NSString *)content;
- (void)setContent:(NSString *)value;

- (NSString *)dueDate;
- (void)setDueDate:(NSString *)value;

- (BOOL)completed;
- (void)setCompleted: (BOOL)value;

- (NSString *)completionDate;

- (Task *)parentTask;
- (void)setParentTask:(Task *)task;

- (void)saveToDb;
- (void)removeFromDb;

- (NSComparisonResult)compareTask:(Task *)value;
- (NSComparisonResult)compareTaskByCompletionDate:(Task *)value;

/*- (void)loadSubTasksCount;
- (void)loadCompletedSubTasksCount;

- (void)loadFromDb;
*/

+ (void)createTask:(Task *)task;


@end
