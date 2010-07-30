
#import "TaskViewController.h"
#import "AppDelegate.h"
#import "Consts.h"

#import "Task.h"
#import "Tasks.h"
#import "TaskViewCell.h"
#import "TaskViewCategoryPickerDelegate.h"
#import "TaskViewToolbarItemsDelegate.h"
#import "TaskViewActionSheetDelegate.h"
#import "TaskViewTableViewDelegate.h"
#import "TaskViewTableViewDataSource.h"
#import "Category.h"
#import "Categories.h"
#import "MyButton.h"

enum tagSections {
	OPEN_SECTION = 0,
	CLOSED_SECTION,
	LAST_NIL_SECTION
};

@implementation TaskViewController

@synthesize tableView;
@synthesize categoryPicker;

#pragma mark Bottom Toolbar methods

- (UIButton *)createCategoryButton {
	UIButton *button = [MyButton buttonWithType:UIButtonTypeCustom];
	button.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
	button.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
	button.backgroundColor = [UIColor clearColor];
	button.showsTouchWhenHighlighted = YES;
	button.titleLabel.font = [UIFont systemFontOfSize:15];
	button.frame = CGRectMake(0, 0, 160, 40);
	[button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
	[button addTarget:toolbarItemsTarget action:@selector(showCategoryPicker:) forControlEvents:UIControlEventTouchDown];
	return [button retain];
}

- (void)createToolbarItems {
	if (toolbarItemsTarget == nil) {
		toolbarItemsTarget = [[TaskViewToolbarItemsDelegate alloc] init:self];
	}

	// Bottom toolbar items
	
    UIBarButtonItem *addItem =
		[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd
													  target:toolbarItemsTarget 
													  action:@selector(actionAddTask:)];
	
	UIBarButtonItem *flexItem =
		[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace 
													  target:nil
													  action:nil];
	UIBarButtonItem *flexItem2 =
		[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace 
                                                  target:nil
                                                  action:nil];
    
	UIBarButtonItem *moreViewItem = 
		[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"moreviews.png"]
										 style:UIBarButtonItemStylePlain
										target:toolbarItemsTarget
										action:@selector(viewMoreScreen:)];


	categoryButton = [self createCategoryButton];
	UIBarButtonItem *categoryItem = [[UIBarButtonItem alloc] initWithCustomView:categoryButton];
	
	NSArray *items = [NSArray arrayWithObjects: 
					  moreViewItem, 
					  flexItem, 
					  categoryItem, 
					  flexItem2, 
					  addItem, nil];
	
	[toolbar setItems:items animated:NO];

	[categoryItem release];
	[moreViewItem release];
	[addItem release];
	[flexItem release];
    [flexItem2 release];
	
	// Top toolbar items
	
	if (parentTask == nil) {
		UIBarButtonItem *todayViewItem = 
		[[UIBarButtonItem alloc] initWithTitle:@"Today"
										 style:UIBarButtonItemStyleBordered
										target:toolbarItemsTarget
										action:@selector(viewTodayView:)];
		self.navigationItem.leftBarButtonItem = todayViewItem;
		[todayViewItem release];
	}
	
    self.navigationItem.rightBarButtonItem = self.editButtonItem;
	
}

#pragma mark Category Picker

- (void)createPicker {
	if (categoryPickerDelegate == nil) {
		categoryPickerDelegate = [[TaskViewCategoryPickerDelegate alloc] init:self];
	}
    if (categoryPicker == nil) {
        categoryPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
        CGSize pickerSize = [categoryPicker sizeThatFits:CGSizeZero];
        CGRect contentFrame = [self.view bounds];
        CGRect frame = CGRectMake(0, 
                                  contentFrame.origin.y + contentFrame.size.height,
                                  pickerSize.width,
                                  pickerSize.height);
        categoryPicker.frame = frame;
        
        categoryPicker.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        categoryPicker.delegate = categoryPickerDelegate;
        categoryPicker.showsSelectionIndicator = YES;	// note this is default to NO
        categoryPicker.alpha = 0.9;
        
        [self.view addSubview:categoryPicker];
    }
}

#pragma mark UIView methods

- (void)setEditing:(BOOL)editing animated:(BOOL)animated {
	[tableView setEditing:editing animated:animated];
	[super setEditing:editing animated:animated];
}

- (void)createContentView {
	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	UIView *contentView = [[UIView alloc] initWithFrame:frame];
	self.view = contentView;
	[contentView release];
}

- (void)createTableView {
    if (tableViewDataSource == nil) {
		tableViewDataSource = [[TaskViewTableViewDataSource alloc] init:self];
	}
	if (tableViewDelegate == nil) {
		tableViewDelegate = [[TaskViewTableViewDelegate alloc] init:self];
	}
    
	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	frame.origin.y = 0;
	frame.size.height = frame.size.height - 2 * TOOLBAR_HEIGHT;
	
	tableView = [[UITableView alloc] initWithFrame:frame 
											 style:UITableViewStylePlain];	
	tableView.delegate = tableViewDelegate;
	tableView.dataSource = tableViewDataSource;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	
	[self.view addSubview:tableView];
}

- (void)createToolbar {
	toolbar = [[UIToolbar alloc] init];
	toolbar.barStyle = UIBarStyleDefault;
	
	[toolbar sizeToFit];
	CGFloat toolbarHeight = [toolbar frame].size.height;
	CGRect mainViewBounds = self.view.bounds;
	[toolbar setFrame:CGRectMake(CGRectGetMinX(mainViewBounds),
								 CGRectGetMinY(mainViewBounds) + CGRectGetHeight(mainViewBounds) - (toolbarHeight * 2.0) + 2.0,
								 CGRectGetWidth(mainViewBounds),
								 toolbarHeight)];
	
	[self.view addSubview:toolbar];
}

- (void)loadView {
    [self createContentView];
    [self createToolbar];
    [self createTableView];
	[self createPicker];
	[self createToolbarItems];	
}

- (void)viewWillAppear:(BOOL)animated {
	NSLog(@"viewWillAppear %@", self);
	[super viewWillAppear:animated];
	[self reloadDataFromDb];
	[self updateCategoryButtonTitle];
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    
	[parentTask release]; parentTask = nil;
    [toolbar release]; toolbar = nil;
    [tableView release]; tableView = nil;
	[categoryButton release]; categoryButton = nil;
    [categoryPicker release]; categoryPicker = nil;

	[toolbarItemsTarget release]; toolbarItemsTarget = nil;
	[categoryPickerDelegate release]; categoryPickerDelegate = nil;
	[actionSheetDelegate release]; actionSheetDelegate = nil;
	[tableViewDelegate release]; tableViewDelegate = nil;
	[tableViewDataSource release]; tableViewDataSource = nil;
    
    [super dealloc];    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return NO;
}

#pragma mark Other instance methods

- (void)updateCategoryButtonTitle {
	NSString *title;
	int categoryId = [[Categories allCategories] selectedCategoryId];
	if (categoryId == ALL_CATEGORY) {
		title = @"<All Tasks>";
	} else if (categoryId == NO_CATEGORY) {
		title = @"<No Category>";
	} else {
		Category *category = [(Category *) [Category alloc] initWithId:categoryId];
		title = category.categoryName;
		[category release];
	}
	[categoryButton setTitle:title forState:UIControlStateNormal];
}

- (void)hideCategoryPicker {
	[toolbarItemsTarget showCategoryPicker:nil];
}

- (void)moveCellToNewSection: (TaskViewCell *)cell {

	Task *task = cell.task;
	UpdateTaskResult *result = [tableViewDataSource allocUpdateDataResultByTask:task];
	
	NSIndexPath *currentIndexPath = [tableView indexPathForCell: cell];
	NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:result.insertPosition inSection:result.newSection];

	NSArray *insertIndexPaths = [NSArray arrayWithObjects:newIndexPath, nil];
	NSArray *deleteIndexPaths = [NSArray arrayWithObjects:currentIndexPath, nil];
	
	[tableView beginUpdates];
    [tableView deleteRowsAtIndexPaths:deleteIndexPaths withRowAnimation:UITableViewRowAnimationFade];	
    [tableView insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
	[tableView endUpdates];
	
	[cell layoutLabels];

	// All tasks are completed
	if ((result.oldSection == 0) && ([tableViewDataSource openTasksCount] == 0)) {
		if (actionSheetDelegate == nil)
			actionSheetDelegate = [[TaskViewActionSheetDelegate alloc] init:self];
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"All tasks has completed, do you want to complete related parent tasks ?"
																 delegate:actionSheetDelegate 
														cancelButtonTitle:@"No" 
												   destructiveButtonTitle:@"Yes" 
														otherButtonTitles:nil];
		actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
		[actionSheet showInView:self.view];
		[actionSheet release];
	}

	if ((result.newSection == 0) && ([tableViewDataSource openTasksCount] > 0)) {
		Task *task = parentTask;
		while(task != nil) {
			if (task.completed) {
				task.completed = NO;
			}
			task = task.parentTask;
		}
	}
	
	[result release];
}

- (Task *)parentTask {
	return parentTask;
}

- (void)setParentTask:(Task *)value {
	[parentTask release];
	parentTask = [value retain];
	self.title = parentTask.content;
}


- (void)reloadDataFromDb {
	[tableViewDataSource loadTasks];
	[tableView reloadData];
}

/*
- (void)reloadData {
	[tableView reloadData];
}

- (void) addTask:(Task *)task {
	[tableViewDataSource addTask:task];
	[tableView reloadData];
}

- (void)sortTasks {
	[tableViewDataSource sortTasks];
}
*/
- (int)openTasksCount {
	return [tableViewDataSource openTasksCount];
}

- (Tasks *)tasksAtSection:(int)section {
	return [tableViewDataSource tasksAtSection:section];
}

@end