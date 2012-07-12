
#import "TodayViewTableViewDelegate.h"
#import "DetailViewController.h"
#import "TodayViewController.h"
#import "TodayViewCell.h"
#import "Consts.h"
#import "Task.h"

@implementation TodayViewTableViewDelegate

@synthesize todayViewController;


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	TodayViewCell *cell = (TodayViewCell *)[tableView cellForRowAtIndexPath:indexPath];
	
	DetailViewController *controller = [[DetailViewController alloc] init];
	controller.task = cell.task;
	controller.readOnly = YES;
	[controller loadView];
	controller.title = @"Task Detail";
	[todayViewController.navigationController pushViewController:controller animated:YES];
	
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	Task *task = [todayViewController taskAt:indexPath];
	
	CGSize titleSize = [task.content sizeWithFont:[UIFont systemFontOfSize:CONTENT_FONT_SIZE]];
	
	int textWidth = SCREEN_WIDTH - CHECK_BUTTON_SIZE - 8;
	
	if (task.subTasksCount > 0) {
		NSString *subTaskText = [NSString stringWithFormat:@"[%d/%d]", 
								 task.completedSubTasksCount, task.subTasksCount];
		
		CGSize size = [subTaskText sizeWithFont:[UIFont systemFontOfSize:SUBTASK_FONT_SIZE]];
		textWidth -= CHECK_BUTTON_SIZE;
		textWidth -= size.width;
	}
	
	if (titleSize.width > textWidth) {
		return DOUBLE_LINE_CELL_HEIGHT;
	}
	
	if ([task.content rangeOfCharacterFromSet:[NSCharacterSet newlineCharacterSet]].length > 0) {
		return DOUBLE_LINE_CELL_HEIGHT;
	}
	
	return SINGLE_LINE_CELL_HEIGHT;
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)aTableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
	Task *task = [todayViewController taskAt:indexPath];
    return (task.subTasksCount > 0) ? UITableViewCellAccessoryDetailDisclosureButton : UITableViewCellAccessoryNone;
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	TodayViewCell *cell = (TodayViewCell *)[tableView cellForRowAtIndexPath:indexPath];
	[cell onAccessoryClick];
}


@end
