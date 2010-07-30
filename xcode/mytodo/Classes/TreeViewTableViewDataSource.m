
#import "TreeViewTableViewDataSource.h"
#import "TreeViewCell.h"
#import "Tasks.h"
#import "Task.h"

@implementation TreeViewTableViewDataSource

@synthesize treeViewController;

- (TreeViewTableViewDataSource *)init {
	[super init];
    indentLevelArray = [[NSMutableArray alloc] init];
    tasks = [Tasks allocTasks:@"parent_id=-1 ORDER BY completed"];

    for (int i = 0; i < tasks.count; ++i) {
        [indentLevelArray addObject:[NSNumber numberWithInt:1]];
    }
    
    Task *rootTask = [[Task alloc] init];
    rootTask.content = @"Root";
    rootTask.taskId = -1;
    [tasks insertTask:rootTask at:0];
    [rootTask release];
    [indentLevelArray insertObject:[NSNumber numberWithInt:0] atIndex:0];
    
	return self;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    [tasks release];
    [indentLevelArray release];
    [super dealloc];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSString *)tableView:(UITableView *)aTableView titleForHeaderInSection:(NSInteger)section {
	return @"";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return tasks.count;
}

- (Task *)taskAt:(NSIndexPath *)path {
    return [tasks taskAtIndex:path.row];
}

- (UITableViewCell *)tableView:(UITableView *)table cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	TreeViewCell *cell = (TreeViewCell *) [table dequeueReusableCellWithIdentifier:TREE_VIEW_CELL_ID];
	
	if (cell == nil) {
		cell = [[[TreeViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:TREE_VIEW_CELL_ID] autorelease];
	}
    
    cell.task = [self taskAt:indexPath];
    cell.indentLevel = [[indentLevelArray objectAtIndex:indexPath.row] intValue];
    cell.treeViewController = treeViewController;
    
	return cell;
}

- (void)openSubTasksOf:(Task *)task withIn:(UITableView *)tv {
    Tasks *subTasks = [Tasks allocTasks:[NSString stringWithFormat:@"parent_id=%d ORDER BY completed", task.taskId]];
    int index = [tasks indexOfTask:task];
    int indentLevel = [[indentLevelArray objectAtIndex:index] intValue];
    NSMutableArray *pathArray = [[NSMutableArray alloc] init];
    for (int i = 0; i < subTasks.count; ++i) {
        Task *t = [subTasks taskAtIndex:i];
        int newIndex = index + 1 + i;
        [tasks insertTask:t at:newIndex];
        [indentLevelArray insertObject:[NSNumber numberWithInt:indentLevel + 1] atIndex:newIndex];
        [pathArray addObject:[NSIndexPath indexPathForRow:newIndex inSection:0]];
    }
    [subTasks release];
    [tv insertRowsAtIndexPaths:pathArray withRowAnimation:UITableViewRowAnimationTop];
    [pathArray release];
}

- (void)closeSubTasksOf:(Task *)task withIn:(UITableView *)tv {
    int index = [tasks indexOfTask:task];
    if (index + 1 == [indentLevelArray count]) return;
    
    int indentLevel = [[indentLevelArray objectAtIndex:index] intValue];

    int i = 1;
    int subTaskIndentLevel = [[indentLevelArray objectAtIndex:index + 1] intValue];
    NSMutableArray *pathArray = [[NSMutableArray alloc] init];
    while (subTaskIndentLevel > indentLevel) {
        Task *t = [tasks taskAtIndex:index + 1];
        [tasks removeTask:t];
        [indentLevelArray removeObjectAtIndex:index + 1];
        [pathArray addObject:[NSIndexPath indexPathForRow:index + i inSection:0]]; i ++;
        if (index + 1 == [indentLevelArray count]) {
            break;
        }
        subTaskIndentLevel = [[indentLevelArray objectAtIndex:index + 1] intValue];
    }
    [tv deleteRowsAtIndexPaths:pathArray withRowAnimation:UITableViewRowAnimationBottom];
    [pathArray release];
}

@end
