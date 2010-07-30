
#import <sqlite3.h>
#import "Task.h"
#import "DbManager.h"
#import "Consts.h"
#import "AppGlobal.h"
#import "TaskCache.h"

@implementation TaskData

@synthesize taskId;
@synthesize parentId;
@synthesize categoryId;
@synthesize priority;
@synthesize completed;
@synthesize content;
@synthesize completionDate;
@synthesize dueDate;
@synthesize subTasksCount;
@synthesize completedSubTasksCount;

- (void)dealloc {
	NSLog(@"dealloc %@ with id %d", self, taskId);
	[content release];
	[completionDate release];
	[dueDate release];
	[super dealloc];
}

- (void)loadSubTasksCount{
    static sqlite3_stmt *subtaks_count_statement = nil;
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *countSQL = @"SELECT count(*) FROM task WHERE parent_id=?";
	[instance prepareStatement:&subtaks_count_statement sql:countSQL];
	
	sqlite3_bind_int(subtaks_count_statement, 1, taskId);
	
	if (sqlite3_step(subtaks_count_statement) == SQLITE_ROW) {
		subTasksCount = sqlite3_column_int(subtaks_count_statement, 0);
	}
	
	sqlite3_reset(subtaks_count_statement);
	
	hasLoadedSubTasksCount = YES;
}

- (void)loadCompletedSubTasksCount {
    static sqlite3_stmt *completed_subtasks_count_statement = nil;
	
	if (subTasksCount > 0) {
		DbManager *instance = [DbManager sharedInstance];
		static NSString *countCompletedSQL = @"SELECT count(*) FROM task WHERE parent_id=? AND completed=1";
		[instance prepareStatement:&completed_subtasks_count_statement sql:countCompletedSQL];
		
		sqlite3_bind_int(completed_subtasks_count_statement, 1, taskId);
		
		if (sqlite3_step(completed_subtasks_count_statement) == SQLITE_ROW) {
			completedSubTasksCount = sqlite3_column_int(completed_subtasks_count_statement, 0);
		}
		
		sqlite3_reset(completed_subtasks_count_statement);
	}
	
	hasLoadedCompletedSubTasksCount = YES;
}

- (void)loadFromDb {
    static sqlite3_stmt *select_statement = nil;
	if (hasLoaded) return;
	if (taskId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"SELECT parent_id, content, priority, completed, completion_date, category_id, due_date FROM task WHERE task_id=?";
	[instance prepareStatement:&select_statement sql:sql];
	
	sqlite3_bind_int(select_statement, 1, taskId);
	
	if (sqlite3_step(select_statement) == SQLITE_ROW) {
		self.parentId = sqlite3_column_int(select_statement, 0);
		
		char *str = (char *)sqlite3_column_text(select_statement, 1);
		self.content = (str) ? [NSString stringWithUTF8String:str] : @"";
				
		self.priority = sqlite3_column_int(select_statement, 2);
		self.completed = sqlite3_column_int(select_statement, 3);
		
		str = (char *)sqlite3_column_text(select_statement, 4);
        self.completionDate = (str) ? [NSString stringWithUTF8String:str] : @"";
        
        self.categoryId = sqlite3_column_int(select_statement, 5);
		
		str = (char *)sqlite3_column_text(select_statement, 6);
        self.dueDate = (str) ? [NSString stringWithUTF8String:str] : @"";
	}
	
	sqlite3_reset(select_statement);

	hasLoaded = YES;
}

- (int) priority {
	if (!hasLoaded) [self loadFromDb];
	return priority;
}

- (int) parentId {
	if (!hasLoaded) [self loadFromDb];
	return parentId;
}

- (int)categoryId {
	if (!hasLoaded) [self loadFromDb];
    return categoryId;
}

- (NSString *)content {
	if (!hasLoaded) [self loadFromDb];
	return content;
}

- (NSString *)dueDate {
	if (!hasLoaded) [self loadFromDb];
	return dueDate;
}

- (NSString *)completionDate {
	if (!hasLoaded) [self loadFromDb];
	return completionDate;
}

- (int)completed {
	if (!hasLoaded) [self loadFromDb];
	return completed;
}

- (int)subTasksCount {
	if (!hasLoadedSubTasksCount) [self loadSubTasksCount];
	return subTasksCount;
}

- (int)completedSubTasksCount {
	if (!hasLoadedCompletedSubTasksCount) [self loadCompletedSubTasksCount];
	return completedSubTasksCount;
}

@end

@implementation Task

@synthesize open;

- (Task *)init {
	[super init];
	data = [[TaskData alloc] init];
	data.taskId = -1;
	return self;
}

- (Task *)initWithId:(int)value {
	[super init];
#ifdef ENABLED_CACHE
	data = [[[TaskCache sharedInstance] taskDataWithId:value] retain];
#else
	data = [[TaskData alloc] init];
	data.taskId = value;
#endif
	NSLog(@"init %@ with id %d", self, value);
	return self;
}

- (void) dealloc {
	NSLog(@"dealloc %@ with id %d", self, data.taskId);
	[data release];
	[parentTask release]; parentTask = nil;
	[super dealloc];
}

#pragma mark DB Methods

- (void)saveToDb {
    static sqlite3_stmt *update_statement = nil;
	
	if (data.taskId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"UPDATE task SET content = ?, priority = ?, category_id = ?, due_date = ?, parent_id = ? WHERE task_id = ?";
	[instance prepareStatement:&update_statement sql:sql];
	
	sqlite3_bind_text(update_statement, 1, [data.content UTF8String], -1, SQLITE_TRANSIENT);
	sqlite3_bind_int(update_statement, 2, data.priority);
	sqlite3_bind_int(update_statement, 3, data.categoryId);
	sqlite3_bind_text(update_statement, 4, [data.dueDate UTF8String], -1, SQLITE_TRANSIENT);
	sqlite3_bind_int(update_statement, 5, data.parentId);
	sqlite3_bind_int(update_statement, 6, data.taskId);
	
	int success = sqlite3_step(update_statement);
	
	sqlite3_reset(update_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];	
}

- (void)removeFromDb {
    static sqlite3_stmt *delete_statement = nil;
    static sqlite3_stmt *delete_subtasks_statement = nil;
	
	if (data.taskId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sqlSub = @"DELETE FROM task WHERE parent_id = ?";
	
	[instance prepareStatement:&delete_subtasks_statement sql:sqlSub];
	
	sqlite3_bind_int(delete_subtasks_statement, 1, data.taskId);
	int success = sqlite3_step(delete_subtasks_statement);
	
	sqlite3_reset(delete_subtasks_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];	
	
	
	static NSString *sql = @"DELETE FROM task WHERE task_id = ?";
	[instance prepareStatement:&delete_statement sql:sql];
	
	sqlite3_bind_int(delete_statement, 1, data.taskId);
	success = sqlite3_step(delete_statement);
	
	sqlite3_reset(delete_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];	
}

- (void) saveCompletedToDb {
    static sqlite3_stmt *update_completed_statement = nil;

	if (data.taskId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];

	NSDateFormatter *formatter = [AppGlobal dateTimeFormatter];
	NSDate *nowDate = [[NSDate alloc] init];
	NSString *now = [formatter stringFromDate:nowDate];
	[nowDate release];
	
	static NSString *sql = @"UPDATE task SET completed = ?, completion_date = ? WHERE task_id = ?";
	[instance prepareStatement:&update_completed_statement sql:sql];
	
	sqlite3_bind_int(update_completed_statement, 1, data.completed);
	sqlite3_bind_text(update_completed_statement, 2, [now UTF8String], -1, SQLITE_TRANSIENT);
	sqlite3_bind_int(update_completed_statement, 3, data.taskId);
	
	int success = sqlite3_step(update_completed_statement);
	
	sqlite3_reset(update_completed_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];

	data.completionDate = now;
}

#pragma mark Getters/Setters

- (int) taskId {
	return data.taskId;
}

- (void) setTaskId:(int)value {
	NSAssert((data.taskId <= 0), @"Error: cannot set a task's ID when it has one");
	data.taskId = value;

#ifdef ENABLED_CACHE
	[[TaskCache sharedInstance] setTaskData:data key:value]; // update cache
	if (data.parentId > 0) {
		TaskData *parentData = [[TaskCache sharedInstance] taskDataWithIdOrNil:data.parentId];
		if (parentData) {
			parentData.subTasksCount ++;
		}
	}
#endif
}

- (int) priority {
	return data.priority;
}

- (void)setPriority:(int)value {
	data.priority = value;
}

- (int) parentId {
	return data.parentId;
}

- (void)setParentId:(int)value {
	if (data.parentId != value) {
#ifdef ENABLED_CACHE		
		// update cache
		TaskData *parentData = [[TaskCache sharedInstance] taskDataWithIdOrNil:data.parentId];
		if (parentData) {
			parentData.subTasksCount --;
			if (self.completed) {
				parentData.completedSubTasksCount --;
			}
		}
#endif
		data.parentId = value;
#ifdef ENABLED_CACHE	
		// update cache
		parentData = [[TaskCache sharedInstance] taskDataWithIdOrNil:data.parentId];
		if (parentData) {
			parentData.subTasksCount ++;
			if (self.completed) {
				parentData.completedSubTasksCount ++;
			}
		}
#endif
	}
}

- (int)categoryId {
    return data.categoryId;
}

- (void)setCategoryId:(int)value {
    data.categoryId = value;
}


- (NSString *)content {
	return data.content;
}

- (void)setContent:(NSString *)value {
	data.content = value;
}


- (NSString *)dueDate {
	return data.dueDate;
}

- (void)setDueDate:(NSString *)value {
	data.dueDate = value;
}

- (NSString *)completionDate {
	return data.completionDate;
}

- (BOOL)completed {
	return (data.completed > 0);
}

- (void)setCompleted:(BOOL)value {
	if (self.completed != value) { // changed
		if (value) {
			data.completed = 1;
		} else {
			data.completed = 0;
		}
#ifdef ENABLED_CACHE
		TaskData *parentData = nil;
		if (data.parentId > 0) { // data in cache
			parentData = [[TaskCache sharedInstance] taskDataWithIdOrNil:data.parentId];
			if (parentData) {
				if (value) {
					parentData.completedSubTasksCount ++; // update cache
				} else {
					parentData.completedSubTasksCount --; // update cache
				}
			}
		}
#endif
		[self saveCompletedToDb];
	}
}

- (Task *)parentTask {
	if (data.parentId <= 0) {
		return nil;
	}
	if (parentTask == nil) {
		parentTask = [[Task alloc] initWithId:data.parentId];
	}
	return parentTask;
}

- (void)setParentTask:(Task *)task {
	[parentTask release];
	parentTask = [task retain];
}

- (int)subTasksCount {
	return data.subTasksCount;
}

- (int)completedSubTasksCount {
	return data.completedSubTasksCount;
}

#pragma mark Compare Methods

- (NSComparisonResult)compareTask:(Task *)value {
	NSComparisonResult r = [self.dueDate compare:value.dueDate];
	if (r != NSOrderedSame) {
		if ([self.dueDate length] == 0) {
			return NSOrderedDescending;
		}
		if ([value.dueDate length] == 0) {
			return NSOrderedAscending;
		}
		return r;
	}
	
	if (self.priority > value.priority) {
		return NSOrderedAscending;
	}
	if (self.priority < value.priority) {
		return NSOrderedDescending;
	}
	if (self.taskId < value.taskId) {
		return NSOrderedAscending;
	}
	if (self.taskId > value.taskId) {
		return NSOrderedDescending;
	}
	
	return NSOrderedSame;
}

- (NSComparisonResult)compareTaskByCompletionDate:(Task *)value {
    return [value.completionDate compare:self.completionDate];
}

#pragma mark Static Methods

+ (void)createTask:(Task *)task {
    static sqlite3_stmt *insert_statement = nil;

	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"INSERT INTO task VALUES(NULL, ?, ?, ?, '', ?, ?, 0, 0, 0, '', '', '')";
	[instance prepareStatement:&insert_statement sql:sql];
	
	sqlite3_bind_int(insert_statement, 1, task.parentId);
	sqlite3_bind_int(insert_statement, 2, task.categoryId);
	sqlite3_bind_text(insert_statement, 3, [task.content UTF8String], -1, SQLITE_TRANSIENT);
	sqlite3_bind_int(insert_statement, 4, task.priority);
	sqlite3_bind_int(insert_statement, 5, task.completed ? 1 : 0);
	
	int success = sqlite3_step(insert_statement);
	
	sqlite3_reset(insert_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];
	
	task.taskId = [instance lastInsertRowId];
}

@end
