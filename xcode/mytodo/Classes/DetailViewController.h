
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

/*
 Behavior determined by task and parentTask
 
 task != nil:
	Updating task
 
 task == nil:
	parentTask = nil:
		Creating a root task
	parentTask != nil:
		Creating a sub task, open parentTask
 
 */

enum tagSections {
	CONTENT_SECTION = 0,
	CATEGORY_SECTION,
	LAST_NIL_SECTION
};

#define NO_DUE_DATE @"<No Due Date>"

@class TaskViewController;
@class Task;
@class DetailViewTextViewDelegate;
@class DetailViewTableViewDelegate;
@class DetailViewTableViewDataSource;
@class DetailViewCustomDueDatePickerDelegate;

@interface DetailViewController : UIViewController
{
	UITableView	*tableView;
	UIToolbar *toolbar;

	UIDatePicker *dueDatePicker;
	
	UIPickerView *customDueDatePicker;
	DetailViewCustomDueDatePickerDelegate *customDueDatePickerDelegate;

	int dueDateStep;
	UIView *dueDateView;
	UIButton *dueDateButton;
	UIButton *clearDueDateButton;
	
	UITextView *contentTextView;
	UISegmentedControl *prioritySegment;
	UIButton *middleButton;

	Task *task;
	Task *parentTask;
	TaskViewController *taskViewController;
    
	DetailViewTextViewDelegate *textViewDelegate;
    DetailViewTableViewDelegate *tableViewDelegate;
    DetailViewTableViewDataSource *tableViewDataSource;
															
	BOOL userCancelled;
	
	BOOL readOnly;
}

@property (retain) Task *task;
@property (retain) Task *parentTask;
@property (retain) TaskViewController *taskViewController;
@property (readonly) UITextView *contentTextView;
@property (readonly) UISegmentedControl *prioritySegment;
@property (readonly) UIView *dueDateView;
@property (readonly) UIButton *dueDateButton;
@property BOOL readOnly;

- (void)saveCurrentTask;
- (void)createNewTask;
- (void)hideAllPicker;

@end
