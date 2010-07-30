
#import <UIKit/UIKit.h>

@class Task;
@class Tasks;
@class TaskViewCell;
@class TaskViewToolbarItemsDelegate;
@class TaskViewCategoryPickerDelegate;
@class TaskViewActionSheetDelegate;
@class TaskViewTableViewDelegate;
@class TaskViewTableViewDataSource;

@interface TaskViewController : UIViewController 
{
	Task *parentTask;
	
	UITableView *tableView;
	UIToolbar *toolbar;
	
	UIPickerView *categoryPicker;
	UIButton *categoryButton;
	BOOL categoryPickerHidden;
	
	//targets and delegates
	TaskViewToolbarItemsDelegate *toolbarItemsTarget;
	TaskViewCategoryPickerDelegate *categoryPickerDelegate;
	TaskViewActionSheetDelegate *actionSheetDelegate;
	TaskViewTableViewDelegate *tableViewDelegate;
	TaskViewTableViewDataSource *tableViewDataSource;	
}

@property (readonly) UITableView *tableView;
@property (readonly) UIPickerView *categoryPicker;

//- (void)addTask:(Task *)task;

- (void)moveCellToNewSection:(TaskViewCell *)cell;
//- (void)updateDataByTask:(Task *)task;
- (void)updateCategoryButtonTitle;
- (void)hideCategoryPicker;

- (void)setParentTask:(Task *)value;
- (Task *)parentTask;

//- (void)sortTasks;
//- (void)reloadData;
- (void)reloadDataFromDb;

- (int)openTasksCount;
- (Tasks *)tasksAtSection:(int)section;

@end
