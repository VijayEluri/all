
#import "TaskViewTableViewDataSource.h"
#import "TaskViewController.h"
#import "Categories.h"
#import "AppGlobal.h"
#import "TaskViewCell.h"
#import "Tasks.h"
#import "Task.h"

@implementation	UpdateTaskResult	

@synthesize insertPosition;
@synthesize newSection;
@synthesize oldSection;

@end

@implementation TaskViewTableViewDataSource

- (NSString *)buildQueryString:(int)parentId completed:(BOOL)completed add:(NSString *)str{
	NSString *template = @"parent_id=%d AND completed=%d %@"; 
	return [NSString stringWithFormat:template, parentId, completed ? 1 : 0, str];
}

- (void)loadTasks {
	if (dataArray == nil) {
		dataArray = [[NSMutableArray alloc] initWithCapacity:2];
	} else {
		[dataArray removeAllObjects];
	}

	NSString *categoryQueryString = [[Categories allCategories] queryString];
	
	int parentId = -1;
	if (taskViewController.parentTask != nil) {
		parentId = taskViewController.parentTask.taskId;
	}
	
	NSString *completedQueryString = [self buildQueryString:parentId completed:YES add:categoryQueryString];
	NSString *notCompletedQueryString = [self buildQueryString:parentId completed:NO add:categoryQueryString];
	
	Tasks *completedTasks = [Tasks allocTasks:completedQueryString];
	Tasks *notCompletedTasks = [Tasks allocTasks:notCompletedQueryString];
	
	[notCompletedTasks sortUsingSelector:@selector(compareTask:)];
	[completedTasks sortUsingSelector:@selector(compareTaskByCompletionDate:)];
	
	[dataArray addObject:[NSMutableDictionary dictionaryWithObjectsAndKeys:
						  @"Todo", @"caption",
						  notCompletedTasks, @"tasks",
						  nil]];
	[dataArray addObject:[NSMutableDictionary dictionaryWithObjectsAndKeys:
						  @"Completed", @"caption",
						  completedTasks, @"tasks",
						  nil]];	
}

-(id)init:(TaskViewController *)controller {
	self = [super init:controller];
	return self;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
		Tasks *tasks = [taskViewController tasksAtSection:indexPath.section];
		TaskViewCell *targetCustomCell = (TaskViewCell *)[tableView cellForRowAtIndexPath:indexPath];
		[targetCustomCell.task removeFromDb];
		[tasks removeTask:targetCustomCell.task];
		[taskViewController.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }	
}

- (Tasks *)tasksAtSection:(int)section {
	Tasks *tasks = [[dataArray objectAtIndex:section] objectForKey:@"tasks"];
	return tasks;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return dataArray.count;
}

- (NSString *)tableView:(UITableView *)aTableView titleForHeaderInSection:(NSInteger)section {
	return [[dataArray objectAtIndex:section] objectForKey:@"caption"];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	Tasks *tasks = [self tasksAtSection:section];
    return tasks.count;
}

- (NSString *)nowDate {
	if (nowDate == nil) {
		NSDate *now = [[NSDate alloc] init];
		nowDate = [[AppGlobal dateFormatter] stringFromDate:now];
	}
	return nowDate;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TaskViewCell *cell = (TaskViewCell *) [tableView dequeueReusableCellWithIdentifier:TASK_CELL_ID];
    if (cell == nil) {
        cell = [[TaskViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:TASK_CELL_ID];
    }
    	
	int section = indexPath.section;
	int row = indexPath.row;
	
	Tasks *tasks = [self tasksAtSection:section];
	Task *task = [tasks taskAtIndex:row];

	cell.nowDate = [self nowDate];
	cell.task = task;
    cell.taskViewController = taskViewController;
    
	return cell;
}

- (UpdateTaskResult *)allocUpdateDataResultByTask:(Task *)task {
	int newSection, oldSection;
	
	if (task.completed) {
		oldSection = 0;
		newSection = 1;
	} else {
		oldSection = 1;
		newSection = 0;
	}
	
	Tasks *oldTasks = [self tasksAtSection:oldSection];
	if (![oldTasks hasTask:task]) {
		return nil;
	}
	
	Tasks *newTasks = [self tasksAtSection:newSection];
	
	int insertPosition = 0;
	
	if (newSection == 0) {
		insertPosition = [newTasks findOrderedIndexForTask:task];
	}
	
	[newTasks insertTask:task at:insertPosition];
	[oldTasks removeTask:task];
	
	UpdateTaskResult *result = [[UpdateTaskResult alloc] init];
	result.insertPosition = insertPosition;
	result.oldSection = oldSection;
	result.newSection = newSection;
	
	return result;
}

- (void)addTask:(Task *)task {
	int section = 0;
	int index = 0;
	
	if (task.completed) {
		section = 1;
	} else {
		section = 0;
	}
	Tasks *tasks = [self tasksAtSection:section];
	if (section == 0)
		index = [tasks findOrderedIndexForTask:task];
	[tasks insertTask:task at:index];
}

- (void)sortTasks {
	Tasks *tasks = [self tasksAtSection:0];
	[tasks sortUsingSelector:@selector(compareTask:)];
}	

- (int)openTasksCount {
	Tasks *tasks = [self tasksAtSection:0];
	return tasks.count;	
}

@end
