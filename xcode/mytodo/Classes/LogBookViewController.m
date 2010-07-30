
#import "LogBookViewController.h"
#import "Tasks.h"
#import "Task.h"
#import "LogBookCell.h"
#import "Consts.h"

@implementation LogBookViewController

- (void)loadView {
	tableView = [[UITableView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] 
											 style:UITableViewStylePlain];	
	tableView.delegate = self;
	tableView.dataSource = self;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	
	self.view = tableView;
	
	sectionTitleArray = [[NSMutableArray alloc] init];
	sectionTasksCountArray = [[NSMutableArray alloc] init];
	sectionStartIndexArray = [[NSMutableArray alloc] init];
	
	tasks = [Tasks allocCompletedTasksOrderByCompletionDate];
	
	if (tasks.count < 8) {
		[sectionTitleArray addObject:@"No date"];
		[sectionTasksCountArray addObject:[NSNumber numberWithInt:tasks.count]];
		[sectionStartIndexArray addObject:[NSNumber numberWithInt:0]];
		return;
	}
	
	NSString *completionDate = [tasks taskAtIndex:0].completionDate;
	if (completionDate.length == 0) {
		[sectionTitleArray addObject:@"No date"];
		[sectionTasksCountArray addObject:[NSNumber numberWithInt:tasks.count]];
		[sectionStartIndexArray addObject:[NSNumber numberWithInt:0]];
		return;
	}
	
	NSString *sectionTitle = [completionDate substringToIndex:10];
	[sectionTitleArray addObject:sectionTitle];
	[sectionStartIndexArray addObject:[NSNumber numberWithInt:0]];
	int tasksInSection = 1;
	
	for (int i = 1; i < tasks.count; ++i) {
		Task *task = [tasks taskAtIndex:i];
		completionDate = task.completionDate;
		if (completionDate.length == 0) {
			[sectionTasksCountArray addObject:[NSNumber numberWithInt:tasksInSection]];
			[sectionTitleArray addObject:@"No date"];
			[sectionStartIndexArray addObject:[NSNumber numberWithInt:i]];
			tasksInSection = tasks.count - i;
			break;
		}
		
		NSString *datePart = [completionDate substringToIndex:10];
		if ([sectionTitle isEqualToString:datePart]) {
			tasksInSection ++;
		} else {
			[sectionTasksCountArray addObject:[NSNumber numberWithInt:tasksInSection]];
			tasksInSection = 1;
			sectionTitle = datePart;
			[sectionTitleArray addObject:sectionTitle];
			[sectionStartIndexArray addObject:[NSNumber numberWithInt:i]];
		}
	}
	[sectionTasksCountArray addObject:[NSNumber numberWithInt:tasksInSection]];
	
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return sectionTitleArray.count;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return [sectionTitleArray objectAtIndex:section];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return [[sectionTasksCountArray objectAtIndex:section] intValue];
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)aTableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellAccessoryNone;
}

- (UITableViewCell *)tableView:(UITableView *)table cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    LogBookCell *cell = (LogBookCell *) [tableView dequeueReusableCellWithIdentifier:LOG_BOOK_CELL_ID];
    if (cell == nil) {
        cell = [[[LogBookCell alloc] initWithFrame:CGRectZero reuseIdentifier:LOG_BOOK_CELL_ID] autorelease];
    }
	
	int startIndex = [[sectionStartIndexArray objectAtIndex:indexPath.section] intValue];
	Task *task = [tasks taskAtIndex:startIndex + indexPath.row];

	[cell setTask:task];
	
	return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	int startIndex = [[sectionStartIndexArray objectAtIndex:indexPath.section] intValue];
	Task *task = [tasks taskAtIndex:startIndex + indexPath.row];
	
	CGSize titleSize = [task.content sizeWithFont:[UIFont systemFontOfSize:CONTENT_FONT_SIZE]];
	int textWidth = SCREEN_WIDTH - CHECK_BUTTON_SIZE;
		
	if (titleSize.width > textWidth) {
		return DOUBLE_LINE_CELL_HEIGHT;
	}

	if ([task.content rangeOfCharacterFromSet:[NSCharacterSet newlineCharacterSet]].length > 0) {
		return DOUBLE_LINE_CELL_HEIGHT;
	}
	
	return SINGLE_LINE_CELL_HEIGHT;
}

- (void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[tv deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[tasks release];
	[tableView release];
	[sectionTitleArray release];
	[sectionTasksCountArray release];
	[sectionStartIndexArray release];
    [super dealloc];
}


@end
