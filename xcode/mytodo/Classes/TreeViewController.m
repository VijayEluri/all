
#import "TreeViewController.h"
#import "TreeViewTableViewDelegate.h"
#import "TreeViewTableViewDataSource.h"
#import "Consts.h"
#import "Task.h"

@implementation TreeViewController

@synthesize taskToMove;

- (void)createContentView {
	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	UIView *contentView = [[UIView alloc] initWithFrame:frame];
	self.view = contentView;
	[contentView release];
}

- (void)actionCancel:(id)sender {
	[self.parentViewController dismissModalViewControllerAnimated:YES];
}

- (void)createToolbar {
	toolbar = [[UIToolbar alloc] init];
	toolbar.barStyle = UIBarStyleDefault;
	
	[toolbar sizeToFit];
	CGFloat toolbarHeight = [toolbar frame].size.height;
	CGRect mainViewBounds = self.view.bounds;
	[toolbar setFrame:CGRectMake(CGRectGetMinX(mainViewBounds),
								 CGRectGetMinY(mainViewBounds) + CGRectGetHeight(mainViewBounds) - (toolbarHeight) + 2.0,
								 CGRectGetWidth(mainViewBounds),
								 toolbarHeight)];
	
	[self.view addSubview:toolbar];
	
    UIBarButtonItem *cancelItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
																				target:self 
																				action:@selector(actionCancel:)];
	UIBarButtonItem *flexItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace
																			  target:nil
																			  action:nil];
	
	NSArray *items = [NSArray arrayWithObjects: cancelItem, flexItem, nil];
	[toolbar setItems:items animated:NO];
	[cancelItem release];
	[flexItem release];
}

- (void)createTableView {
	CGRect frame = [self.view frame];
	frame.origin.y = 0;
	frame.size.height = frame.size.height - TOOLBAR_HEIGHT;

	tableViewDelegate = [[TreeViewTableViewDelegate alloc] init];
	tableViewDelegate.treeViewController = self;
	
	tableViewDataSource = [[TreeViewTableViewDataSource alloc] init];
	tableViewDataSource.treeViewController = self;
	
	tableView = [[UITableView alloc] initWithFrame:frame
											 style:UITableViewStylePlain];	
	tableView.delegate = tableViewDelegate;
	tableView.dataSource = tableViewDataSource;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	//    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
	
	[self.view addSubview:tableView];
}

- (void)loadView {
	[self createContentView];
	[self createTableView];
	[self createToolbar];
}

- (Task *)taskAt:(NSIndexPath *)path {
	return [tableViewDataSource taskAt:path];
}

- (void)openSubTasks:(Task *)task {
    [tableViewDataSource openSubTasksOf:task withIn:tableView];
}

- (void)closeSubTasks:(Task *)task {
    [tableViewDataSource closeSubTasksOf:task withIn:tableView];
}

- (void)moveTaskTo:(Task *)parentTask {
    taskToMove.parentId = parentTask.taskId;
    [taskToMove saveToDb];
    
    [self.parentViewController dismissModalViewControllerAnimated:YES];
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [tableViewDelegate release];
    [tableViewDataSource release];
    [tableView release];
    [taskToMove release];
    [super dealloc];
}


@end
