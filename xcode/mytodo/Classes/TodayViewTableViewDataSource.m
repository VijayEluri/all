
#import "TodayViewTableViewDataSource.h"
#import "TodayViewCell.h"
#import "AppGlobal.h"
#import "Tasks.h"
#import "Task.h"

@implementation TodayViewTableViewDataSource

@synthesize todayViewController;

- (TodayViewTableViewDataSource *)init {
	[super init];
	[self reloadData];
	return self;
}

- (void)dealloc {
	[tasks release];
	[sectionTitleArray release];
	[sectionTasksCountArray release];
	[sectionStartIndexArray release];
	[super dealloc];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return [sectionTitleArray count];
}

- (NSString *)tableView:(UITableView *)aTableView titleForHeaderInSection:(NSInteger)section {
	return [sectionTitleArray objectAtIndex:section];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return [[sectionTasksCountArray objectAtIndex:section] intValue];
}

- (Task *)taskAt:(NSIndexPath *)path {
	int startIndex = [[sectionStartIndexArray objectAtIndex:path.section] intValue];
	Task *t = [tasks taskAtIndex:startIndex + path.row];
	return t;
}

- (UITableViewCell *)tableView:(UITableView *)table cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	TodayViewCell *cell = (TodayViewCell *) [table dequeueReusableCellWithIdentifier:TODAY_VIEW_CELL_ID];
	
	if (cell == nil) {
		cell = [[[TodayViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:TODAY_VIEW_CELL_ID] autorelease];
	}
	
	cell.todayViewController = todayViewController;
	cell.task = [self taskAt:indexPath];
	
	return cell;
}

- (void)reloadData {
	[tasks release];
	tasks = [Tasks allocTasksOrderByDueDate];
	
	NSDate *today = [[NSDate alloc] init];
	NSString* todayDate = [[AppGlobal dateFormatter] stringFromDate:today];
	[today release];
	
	sectionTitleArray = [[NSMutableArray alloc] init];
	sectionTasksCountArray = [[NSMutableArray alloc] init];
	sectionStartIndexArray = [[NSMutableArray alloc] init];
	
	int index = 0;
	NSString *sectionTitle = @"";
	int sectionCount = 0;
	BOOL hasNoDueDate = NO;
	
	for (; index < tasks.count; ++index) {
		Task *t = [tasks taskAtIndex:index];
		NSString *title;
		if ([t.dueDate length] == 0) {
			title = @"No Due Date";
			hasNoDueDate = YES;
		} else {
			switch ([t.dueDate compare:todayDate]) {
				case NSOrderedDescending: // due after today
					title = t.dueDate;
					break;
				case NSOrderedAscending: // due before today
					title = @"Overdue";
					break;
				default: //due today
					title = @"Today";
					break;
			}
		}
		
		if ([sectionTitle compare:title] != NSOrderedSame) {
			[sectionTitleArray addObject:title];
			[sectionStartIndexArray addObject:[NSNumber numberWithInt:index]];
			if ([sectionTitle length] != 0) {
				[sectionTasksCountArray addObject:[NSNumber numberWithInt:sectionCount]];
			}
			sectionTitle = title;
			sectionCount = 1;
		} else {
			sectionCount ++;
		}
		
		if (index == tasks.count - 1) { // last section
			[sectionTasksCountArray addObject:[NSNumber numberWithInt:sectionCount]];
		}
		
	}
	
	// Move data without due date to end of array
	if (hasNoDueDate) {
		[sectionTitleArray addObject:[sectionTitleArray objectAtIndex:0]];
		[sectionTitleArray removeObjectAtIndex:0];
		
		[sectionTasksCountArray addObject:[sectionTasksCountArray objectAtIndex:0]];
		[sectionTasksCountArray removeObjectAtIndex:0];
		
		[sectionStartIndexArray addObject:[sectionStartIndexArray objectAtIndex:0]];
		[sectionStartIndexArray removeObjectAtIndex:0];
	}
}

- (int)removeTask:(Task *)task {
	int sectionToRemove = -1;
	int indexToRemove = [tasks indexOfTask:task];
	
	for (int i = 0; i < sectionStartIndexArray.count; ++i) {
		int startIndex = [[sectionStartIndexArray objectAtIndex:i] intValue];
		int count = [[sectionTasksCountArray objectAtIndex:i] intValue];
		if (startIndex + count - 1 < indexToRemove) continue;
		if ((indexToRemove >= startIndex) && (indexToRemove < startIndex + count)) {
			count --;
			[sectionTasksCountArray replaceObjectAtIndex:i withObject:[NSNumber numberWithInt:count]];
			if (count == 0) {
				sectionToRemove = i;
			}
			continue;
		}
		startIndex --;
		[sectionStartIndexArray replaceObjectAtIndex:i withObject:[NSNumber numberWithInt:startIndex]];
	}
	
	if (sectionToRemove >= 0) {
		[sectionTitleArray removeObjectAtIndex:sectionToRemove];
		[sectionStartIndexArray removeObjectAtIndex:sectionToRemove];
		[sectionTasksCountArray removeObjectAtIndex:sectionToRemove];
	}
	[tasks removeTask:task];
	return sectionToRemove;
}

@end
