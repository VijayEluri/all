
#import <UIKit/UIKit.h>

#define TASK_CELL_ID @"TaskCellId"

@class Task;
@class TaskViewController;

@interface TaskViewCell : UITableViewCell <UIActionSheetDelegate>
{
	UIButton *completionCheckButton;
	UILabel *taskContentLabel;
	UILabel *subTaskCountLabel;
	UILabel *dateLabel;
	NSString *nowDate;
		
	Task *theTask;
	TaskViewController *taskViewController;
}

@property (retain) Task *task;
@property (assign) TaskViewController *taskViewController; 
@property (retain) NSString *nowDate;
		   
- (void)layoutLabels;
- (void)onAccessoryClick;

@end
