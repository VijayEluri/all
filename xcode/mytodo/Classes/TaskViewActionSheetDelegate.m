
#import "TaskViewActionSheetDelegate.h"
#import "TaskViewController.h"
#import "Task.h"

@implementation TaskViewActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
	// the user clicked one of the OK/Cancel buttons
	if (buttonIndex == 0) {
		Task *task = taskViewController.parentTask;
		while(task != nil) {
			if (!task.completed) {
				task.completed = YES;
			}
			task = task.parentTask;
			if (task.completedSubTasksCount != task.subTasksCount) {
				break;
			} 
		}
	}	
}

@end
