
#import "TaskViewTableViewDelegate.h"
#import "TaskViewController.h"
#import "DetailViewController.h"
#import "Consts.h"
#import "TaskViewCell.h"
#import "Tasks.h"
#import "Task.h"

@implementation TaskViewTableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	TaskViewCell *cell = (TaskViewCell *)[tableView cellForRowAtIndexPath:indexPath];
	
	DetailViewController *controller = [[DetailViewController alloc] init];
	controller.task = cell.task;
	controller.parentTask = taskViewController.parentTask;
	[controller setTaskViewController:taskViewController];
	[controller loadView];
	controller.title = @"Task Detail";
	[taskViewController.navigationController pushViewController:controller animated:YES];
	
	[controller release];
	
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}


- (UITableViewCellAccessoryType)tableView:(UITableView *)aTableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
    return (aTableView.editing) ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDetailDisclosureButton;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)aTableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
	return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	TaskViewCell *cell = (TaskViewCell *)[tableView cellForRowAtIndexPath:indexPath];
	[cell onAccessoryClick];
}

/*
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
	return SECTION_HEIGHT;
}*/

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	int section = indexPath.section;
	int row = indexPath.row;
	
	Tasks *tasks = [taskViewController tasksAtSection:section];
	Task *task = [tasks taskAtIndex:row];
	
	CGSize titleSize = [task.content sizeWithFont:[UIFont systemFontOfSize:CONTENT_FONT_SIZE]];
	
	int textWidth = SCREEN_WIDTH - 2 * CHECK_BUTTON_SIZE;
	
	if (task.subTasksCount > 0) {
		NSString *subTaskText = [NSString stringWithFormat:@"[%d/%d]", 
								 task.completedSubTasksCount, task.subTasksCount];
		
		CGSize size = [subTaskText sizeWithFont:[UIFont systemFontOfSize:SUBTASK_FONT_SIZE]];
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


@end
