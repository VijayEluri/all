#import <sqlite3.h>
#import "Tasks.h"
#import "DbManager.h"
#import "Task.h"

@implementation Tasks

- (Tasks *) init {
	self = [super init];
	tasksArray = [[NSMutableArray alloc] init];
	return self;
}

- (Task *) taskAtIndex: (int)value {
	return [tasksArray objectAtIndex:value];
}

- (void) addTask: (Task *)value {
	[tasksArray addObject:value];
}

- (void) removeTask: (Task *)value {
	[tasksArray removeObject:value];
}

- (void) insertTask: (Task *)task at:(int)index {
	[tasksArray insertObject:task atIndex:index];
}

- (BOOL)hasTask:(Task *)task {
	return [tasksArray containsObject:task];
}

- (int) count {
	return [tasksArray count];
}

- (int)indexOfTask:(Task *)value {
	return [tasksArray indexOfObject:value];
}


- (void)sortUsingSelector:(SEL)sel {
	[tasksArray sortUsingSelector:sel];
}

- (int)findOrderedIndexForTask:(Task *)value from:(int)from to:(int)to {	

	Task *task = [self taskAtIndex:from];
	// less than from
	if ([value compareTask:task] == NSOrderedAscending) { 
		return from - 1;
	}

	// larger than to
	task = [self taskAtIndex:to];
	if ([value compareTask:task] == NSOrderedDescending) { 
		return to + 1;
	}

	// equals to the task, or small enough
	if ((to == from) || ((to - from) == 1)) {
		return to;
	}

	int breakPoint = (to + from) / 2;

	int result = [self findOrderedIndexForTask:value from:from to:breakPoint];
	
	if (result < from) {
		return from;
	}
	
	if (result > breakPoint) {
		result = [self findOrderedIndexForTask:value from:breakPoint + 1 to:to];
		if (result < breakPoint + 1) {
			result = breakPoint + 1;
		}
	}
	
	return result;
}

- (int)findOrderedIndexForTask:(Task *)value {
	if (self.count == 0) {
		return 0;
	}
	
	int from = 0;
	int to = self.count - 1;
	int result = [self findOrderedIndexForTask:value from:from to:to];
	if (result < from) result = from;
	return result;
}

+ (Tasks *)allocTasksOrderByDueDate {
    static sqlite3_stmt *select_by_due_date_statement = nil;
	static NSString *sql = @"SELECT task_id FROM task WHERE completed = 0 ORDER BY due_date, priority DESC, task_id";
	DbManager *instance = [DbManager sharedInstance];
	
	Tasks *tasks = [[Tasks alloc] init];
	
	[instance prepareStatement:&select_by_due_date_statement sql:sql];
	while (sqlite3_step(select_by_due_date_statement) == SQLITE_ROW) {
		int primaryKey = sqlite3_column_int(select_by_due_date_statement, 0);
		Task *task = [[Task alloc] initWithId:primaryKey];
		[tasks addTask:task];
	}
	sqlite3_reset(select_by_due_date_statement);
	
	return tasks;
}

+ (Tasks *)allocCompletedTasksOrderByCompletionDate {
    static sqlite3_stmt *select_by_date_statement = nil;
	static NSString *sql = @"SELECT task_id FROM task WHERE completed = 1 ORDER BY completion_date DESC";
	DbManager *instance = [DbManager sharedInstance];
	
	Tasks *tasks = [[Tasks alloc] init];
	
	[instance prepareStatement:&select_by_date_statement sql:sql];
	while (sqlite3_step(select_by_date_statement) == SQLITE_ROW) {
		int primaryKey = sqlite3_column_int(select_by_date_statement, 0);
		Task *task = [[Task alloc] initWithId:primaryKey];
		[tasks addTask:task];
	}
	sqlite3_reset(select_by_date_statement);
	
	return tasks;
}

+ (Tasks *)allocTasks: (Task *)parentTask completed:(BOOL)completed {
	int parentId = 0;
	
	if (parentTask == nil) {
		parentId = -1;
	} else {
		parentId = parentTask.taskId;
	}
	
    static sqlite3_stmt *select_statement = nil;
	static NSString *sql = @"SELECT task_id FROM task WHERE parent_id=? AND completed=?";
	DbManager *instance = [DbManager sharedInstance];
	
	[instance prepareStatement:&select_statement sql:sql];
	
	sqlite3_bind_int(select_statement, 1, parentId);
	sqlite3_bind_int(select_statement, 2, completed ? 1 : 0);
	
	Tasks *tasks = [[Tasks alloc] init];
	
	while (sqlite3_step(select_statement) == SQLITE_ROW) {
		int primaryKey = sqlite3_column_int(select_statement, 0);
		Task *task = [[Task alloc] initWithId:primaryKey];
		[tasks addTask:task];
	}
	
	sqlite3_reset(select_statement);
	
	return tasks;
}

+ (Tasks *)allocTasks: (NSString *)queryString {
	sqlite3_stmt *select_statement = nil;
	NSString *sql = [NSString stringWithFormat:@"SELECT task_id FROM task WHERE %@", queryString];
	DbManager *instance = [DbManager sharedInstance];
	
	[instance prepareStatement:&select_statement sql:sql];
	
	Tasks *tasks = [[Tasks alloc] init];
	
	while (sqlite3_step(select_statement) == SQLITE_ROW) {
		int primaryKey = sqlite3_column_int(select_statement, 0);
		Task *task = [[Task alloc] initWithId:primaryKey];
		[tasks addTask:task];
	}
	
	sqlite3_reset(select_statement);
	sqlite3_finalize(select_statement);
	select_statement = nil;
	return tasks;
	
}


@end
