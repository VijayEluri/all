
#import "DetailViewController.h"
#import "TaskViewController.h"
#import "TreeViewController.h"
#import "AppGlobal.h"
#import "MyButton.h"
#import "Consts.h"
#import "Task.h"

#import "DetailViewTextViewDelegate.h"
#import "DetailViewTableViewDelegate.h"
#import "DetailViewTableViewDataSource.h"
#import "DetailViewCustomDueDatePickerDelegate.h"

@implementation DetailViewController

@synthesize task;
@synthesize parentTask;
@synthesize taskViewController;
@synthesize contentTextView;
@synthesize prioritySegment;
@synthesize dueDateView;
@synthesize dueDateButton;
@synthesize readOnly;

#pragma mark Middle Button

- (void)actionSaveAndNew:(id)sender {
	[self createNewTask];
	contentTextView.text = @"";
	[contentTextView becomeFirstResponder];
}

- (void)createMiddleButton {
	middleButton = [MyButton buttonWithType:UIButtonTypeCustom];
	middleButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
	middleButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
	middleButton.backgroundColor = [UIColor clearColor];
	middleButton.showsTouchWhenHighlighted = YES;
	middleButton.titleLabel.font = [UIFont boldSystemFontOfSize:20];
	middleButton.frame = CGRectMake(0, 0, 120, 40);
	[middleButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
	
	[middleButton addTarget:self action:@selector(actionSaveAndNew:) forControlEvents:UIControlEventTouchDown];
	[middleButton setTitle:@"Save & New" forState:UIControlStateNormal];
	
	self.navigationItem.titleView = middleButton;
}

#pragma mark Toolbar related

- (void)actionCancel:(id)sender {
	userCancelled = YES;
	[self.navigationController popViewControllerAnimated:YES];	
}

- (void)actionMove:(id)sender {
    TreeViewController *controller = [[TreeViewController alloc] init];
    controller.taskToMove = task;
    [controller loadView];
    [self presentModalViewController:controller animated:YES];
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
	
    UIBarButtonItem *cancelItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
																				target:self 
																				action:@selector(actionCancel:)];
	UIBarButtonItem *flexItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace
																			  target:nil
																			  action:nil];
    UIBarButtonItem *moveItem = [[UIBarButtonItem alloc] initWithTitle:@"Move ..."
                                                                 style:UIBarButtonItemStyleBordered
                                                                target:self 
                                                                action:@selector(actionMove:)];
	
	NSArray *items = [NSArray arrayWithObjects: cancelItem, flexItem, moveItem, nil];
	[toolbar setItems:items animated:NO];
}

#pragma mark CustomDueDatePicker

- (void)createCustomDueDatePicker {	
	customDueDatePicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
	customDueDatePicker.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	
	CGSize pickerSize = [customDueDatePicker sizeThatFits:CGSizeZero];
	CGRect contentFrame = [self.view bounds];
	CGRect frame = CGRectMake(0, 
							  contentFrame.origin.y + contentFrame.size.height,
							  pickerSize.width,
							  pickerSize.height);
	
	customDueDatePicker.frame = frame;
	customDueDatePicker.alpha = 0.9;
	customDueDatePicker.showsSelectionIndicator = YES;	// note this is default to NO
	
	[self.view addSubview:customDueDatePicker];
}

#pragma mark DueDatePicker

- (void)actionDueDatePickerChanged:(id)sender {
	NSDateFormatter *formater = [AppGlobal dateFormatter];
	[dueDateButton setTitle:[formater stringFromDate:dueDatePicker.date] forState:UIControlStateNormal];
}

- (void)createDueDatePicker {
	dueDatePicker = [[UIDatePicker alloc] initWithFrame:CGRectZero];
	dueDatePicker.datePickerMode = UIDatePickerModeDate;
	dueDatePicker.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	
	CGSize pickerSize = [dueDatePicker sizeThatFits:CGSizeZero];
	CGRect contentFrame = [self.view bounds];
	CGRect frame = CGRectMake(0, 
							  contentFrame.origin.y + contentFrame.size.height,
							  pickerSize.width,
							  pickerSize.height);
	
	dueDatePicker.frame = frame;
	dueDatePicker.alpha = 0.9;
	[dueDatePicker addTarget:self action:@selector(actionDueDatePickerChanged:) forControlEvents:UIControlEventValueChanged];
	
	[self.view addSubview:dueDatePicker];
}

#pragma mark Show/Hide Picker

- (void)showPicker:(UIView *)picker {
	CGSize pickerSize = [picker sizeThatFits:CGSizeZero];
	CGRect contentFrame = [self.view bounds];
	CGRect frame; 
	
	frame = CGRectMake(contentFrame.origin.x, 
					   contentFrame.origin.y + contentFrame.size.height - pickerSize.height,
					   pickerSize.width,
					   pickerSize.height);
	
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
	picker.frame = frame;
	[UIView commitAnimations];
}

- (void)hidePicker:(UIView *)picker {
	CGSize pickerSize = [picker sizeThatFits:CGSizeZero];
	CGRect contentFrame = [self.view bounds];
	CGRect frame; 
	
	frame = CGRectMake(contentFrame.origin.x, 
					   contentFrame.origin.y + contentFrame.size.height,
					   pickerSize.width,
					   pickerSize.height);
	
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
	picker.frame = frame;
	[UIView commitAnimations];
}

#pragma mark Due date button

- (void)pickDueDate:(id)sender {
	[contentTextView resignFirstResponder];
	self.navigationItem.rightBarButtonItem = nil;
	dueDateStep ++;
	
	if (customDueDatePickerDelegate == nil) {
		customDueDatePickerDelegate = [[DetailViewCustomDueDatePickerDelegate alloc] init:self];
		customDueDatePicker.delegate = customDueDatePickerDelegate;
	}
	
	NSString *currentDueDate;
	switch (dueDateStep) {
		case 1:
			[self hidePicker:dueDatePicker];
			[self showPicker:customDueDatePicker];
			currentDueDate = [dueDateButton titleForState:UIControlStateNormal];
			NSInteger row = [customDueDatePickerDelegate rowOfDate:currentDueDate];
			if (row >= 0) {
				[customDueDatePicker selectRow:row inComponent:0 animated:YES];
			}
			break;
		case 2:
			[self hidePicker:customDueDatePicker];
			[self showPicker:dueDatePicker];
			currentDueDate = [dueDateButton titleForState:UIControlStateNormal];
			if ([currentDueDate compare:NO_DUE_DATE] != NSOrderedSame) {
				NSDateFormatter *dateFormatter = [AppGlobal dateFormatter];
				[dueDatePicker setDate:[dateFormatter dateFromString:currentDueDate] animated:YES];
			}
			break;
		default:
			[self hidePicker:dueDatePicker];
			[self hidePicker:customDueDatePicker];
			dueDateStep = 0;
			break;
	}
}

- (void)clearDueDate:(id)sender {
	[dueDateButton setTitle:NO_DUE_DATE forState:UIControlStateNormal];
}

- (void)createDueDateButton {
	
	dueDateView = [[UIView alloc] initWithFrame:CGRectMake(8, 5, 200, 40)];

	dueDateButton = [MyButton buttonWithType:UIButtonTypeCustom];
	dueDateButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
	dueDateButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
	dueDateButton.backgroundColor = [UIColor clearColor];
	dueDateButton.showsTouchWhenHighlighted = YES;
	dueDateButton.titleLabel.font = [UIFont systemFontOfSize:15];
	dueDateButton.frame = CGRectMake(0, 0, 160, 40);

	[dueDateButton addTarget:self action:@selector(pickDueDate:) forControlEvents:UIControlEventTouchDown];
	[dueDateButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
	if (task) {
		if ([task.dueDate length] > 0) {
			[dueDateButton setTitle:task.dueDate forState:UIControlStateNormal];
		} else {
			[dueDateButton setTitle:NO_DUE_DATE forState:UIControlStateNormal];
		}
	} else {
		[dueDateButton setTitle:NO_DUE_DATE forState:UIControlStateNormal];
	}
	
	[dueDateView addSubview:dueDateButton];
	
	clearDueDateButton = [MyButton buttonWithType:UIButtonTypeCustom];
	clearDueDateButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
	clearDueDateButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
	clearDueDateButton.backgroundColor = [UIColor clearColor];
	clearDueDateButton.showsTouchWhenHighlighted = YES;
	clearDueDateButton.frame = CGRectMake(160, 0, 40, 40);
	[clearDueDateButton setBackgroundImage:[UIImage imageNamed:@"clear.png"] forState:UIControlStateNormal];
	[clearDueDateButton addTarget:self action:@selector(clearDueDate:) forControlEvents:UIControlEventTouchDown];

	[dueDateView addSubview:clearDueDateButton];
}

#pragma mark TextView related

- (void)createTextView {
	if (contentTextView == nil) {
		textViewDelegate = [[DetailViewTextViewDelegate alloc] init:self];
		
		CGRect frame = CGRectMake(10.0, 10.0, 300.0, 100.0);
		
		contentTextView = [[UITextView alloc] initWithFrame:frame];
		contentTextView.textColor = [UIColor blackColor];
		contentTextView.font = [UIFont systemFontOfSize:16.0];
		contentTextView.delegate = textViewDelegate;
		contentTextView.backgroundColor = [UIColor whiteColor];
		
		contentTextView.returnKeyType = UIReturnKeyDefault;
		contentTextView.keyboardType = UIKeyboardTypeDefault;
		
		if (task) {
			contentTextView.text = task.content;
		}
	}
}

#pragma mark Priority Segment related

- (void)setPriority:(id)sender {
	self.navigationItem.rightBarButtonItem = nil;
	[contentTextView resignFirstResponder];
	[self hideAllPicker];
}

- (void)createPrioritySegmentedControl {
	prioritySegment = [[UISegmentedControl alloc] initWithItems:
                       [NSArray arrayWithObjects:@"Low", @"Normal", @"High", @"Urgent", nil]];
	prioritySegment.segmentedControlStyle = UISegmentedControlStyleBar;
	prioritySegment.backgroundColor = [UIColor clearColor];
	
	const int width = 45;
	[prioritySegment setWidth:width forSegmentAtIndex:0];
	[prioritySegment setWidth:width forSegmentAtIndex:2];

	if (task) {
		prioritySegment.selectedSegmentIndex = task.priority;
	} else {
		prioritySegment.selectedSegmentIndex = 1;
	}
	
	[prioritySegment addTarget:self action:@selector(setPriority:) forControlEvents:UIControlEventValueChanged];
}

#pragma mark UIView related

- (void)createContentView {
	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	UIView *contentView = [[UIView alloc] initWithFrame:frame];
	self.view = contentView;
}

- (void)createTableView {
    if (tableViewDelegate == nil) {
        tableViewDelegate = [[DetailViewTableViewDelegate alloc] init];
		tableViewDelegate.detailViewController = self;
    }
    
    if (tableViewDataSource == nil) {
        tableViewDataSource = [[DetailViewTableViewDataSource alloc] init];
        tableViewDataSource.detailViewController = self;
        tableViewDataSource.task = task;
		tableViewDataSource.parentTask = parentTask;
    }

	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	frame.origin.y = 0;
	frame.size.height = frame.size.height - 2 * TOOLBAR_HEIGHT;
	
	tableView = [[UITableView alloc] initWithFrame:frame 
											 style:UITableViewStyleGrouped];	
	tableView.delegate = tableViewDelegate;
	tableView.dataSource = tableViewDataSource;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	
	[self.view addSubview:tableView];
}

- (void)loadView {
	if (task == nil) {
		[self createMiddleButton];
	}
    [self createContentView];

    [self createToolbar];		
	[self createTextView];
	[self createPrioritySegmentedControl];
	[self createDueDateButton];
    [self createTableView];
	[self createDueDatePicker];
	[self createCustomDueDatePicker];

	
	if (task == nil) {
		[textViewDelegate createButton];
		[contentTextView becomeFirstResponder];
	}
	
	if (readOnly) {
		contentTextView.editable = NO;
		[prioritySegment setEnabled:NO];
		[dueDateButton setEnabled:NO];
	}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return NO;
}

- (void)viewWillDisappear:(BOOL)animated {
	NSLog(@"viewWillDisappear %@", self);
	[super viewWillDisappear:animated];
	if (!userCancelled && !readOnly) {
		if (task == nil) {
			[self createNewTask];
		} else {
			[self saveCurrentTask];
		}
	}
}

#pragma mark Other Messages

- (void)openAllParentTasks:(Task *)t {
	while (t != nil) {
		if (t.completed) {
			t.completed = NO;
		}
		t = t.parentTask;
	}
}

- (void) createNewTask {
	NSString *taskContent = [contentTextView.text 
							 stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if ([taskContent length] > 0) {
        Task *t = [[Task alloc] init];
        if (parentTask) {
            [self openAllParentTasks:parentTask];
            t.parentId = parentTask.taskId;
        } else {
            t.parentId = -1;
        }
        t.content = taskContent;
        t.completed = NO;
        t.priority = prioritySegment.selectedSegmentIndex;
        t.categoryId = [tableViewDataSource selectedCategoryId:tableView];
		NSString *currentDueDate = [dueDateButton titleForState:UIControlStateNormal];
		if ([currentDueDate compare:NO_DUE_DATE] != NSOrderedSame) {
			t.dueDate = currentDueDate;
		} else {
			t.dueDate = @"";
		}
        [Task createTask:t];
    }
}

- (void)saveCurrentTask {
	NSString *taskContent = [contentTextView.text 
							 stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if ([taskContent length] > 0) {
        task.content = taskContent;
        task.priority = prioritySegment.selectedSegmentIndex;
        task.categoryId = [tableViewDataSource selectedCategoryId:tableView];
		NSString *currentDueDate = [dueDateButton titleForState:UIControlStateNormal];
		if ([currentDueDate compare:NO_DUE_DATE] != NSOrderedSame) {
			task.dueDate = currentDueDate;
		} else {
			task.dueDate = @"";
		}
        [task saveToDb];
    }
}

- (void)hideAllPicker {
	[self hidePicker:dueDatePicker];
	[self hidePicker:customDueDatePicker];
	dueDateStep = 0;
}

@end
