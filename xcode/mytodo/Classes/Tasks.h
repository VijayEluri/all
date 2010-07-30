
#import <Foundation/Foundation.h>

@class Task;

@interface Tasks : NSObject {
	NSMutableArray *tasksArray;
}

- (Tasks *)init;
- (Task *)taskAtIndex: (int)value;
- (void)addTask: (Task *)value;
- (void)insertTask: (Task *)task at:(int)index;
- (void)removeTask: (Task *)value;
- (BOOL)hasTask:(Task *)task;
- (int)count;
- (void)sortUsingSelector:(SEL)sel;
- (int)findOrderedIndexForTask:(Task *)value;
- (int)indexOfTask:(Task *)value;

+ (Tasks *)allocCompletedTasksOrderByCompletionDate;
+ (Tasks *)allocTasksOrderByDueDate;
+ (Tasks *)allocTasks: (Task *)parentTask completed:(BOOL)completed;
+ (Tasks *)allocTasks: (NSString *)queryString;

@end
