
#import "TreeViewTableViewDelegate.h"
#import "TreeViewController.h"
#import "TreeViewCell.h"
#import "Consts.h"
#import "Task.h"

@implementation TreeViewTableViewDelegate

@synthesize treeViewController;

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[super dealloc];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	TreeViewCell *cell = (TreeViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    parentTask = cell.task;
	
	if (parentTask.taskId == treeViewController.taskToMove.taskId) {
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You cannot move a task into itself."
																 delegate:self 
														cancelButtonTitle:@"Close" 
												   destructiveButtonTitle:nil 
														otherButtonTitles:nil];
		actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
		[actionSheet showInView:treeViewController.view];
		[actionSheet release];
	} else {
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:[NSString stringWithFormat:@"Move task \"%@\" into \"%@\" ?", treeViewController.taskToMove.content, parentTask.content]
																 delegate:self 
														cancelButtonTitle:@"No" 
												   destructiveButtonTitle:@"Yes" 
														otherButtonTitles:nil];
		actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
		[actionSheet showInView:treeViewController.view];
		[actionSheet release];
	}
    	
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 35;
}


- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (actionSheet.numberOfButtons == 1) return;

	if (buttonIndex == 0) {
        [treeViewController moveTaskTo:parentTask];
	}	
}

@end
