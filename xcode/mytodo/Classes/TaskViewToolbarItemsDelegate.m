
#import "TaskViewToolbarItemsDelegate.h"
#import "LogBookViewController.h"
#import "Consts.h"
#import "TaskViewController.h"
#import "DetailViewController.h"
#import "MoreScreensViewController.h"
#import "TodayViewController.h"
#import "StartMenuView.h"

@implementation TaskViewToolbarItemsDelegate

- (id)init:(TaskViewController *)controller {
	[super init:controller];
	categoryPickerHidden = YES;
	startMenuHidden = YES;
	return self;
}

- (void)dealloc {
	[startMenuView release];
	[super dealloc];
}

- (void)showCategoryPicker:(id)sender {
    [taskViewController.categoryPicker reloadAllComponents];
	CGSize pickerSize = [taskViewController.categoryPicker sizeThatFits:CGSizeZero];
	CGRect contentFrame = [taskViewController.view bounds];
	CGRect frame; 
	
	if (categoryPickerHidden) {
		frame = CGRectMake(contentFrame.origin.x, 
						   contentFrame.origin.y + contentFrame.size.height - pickerSize.height - TOOLBAR_HEIGHT,
						   pickerSize.width,
						   pickerSize.height);
	} else {
		frame = CGRectMake(contentFrame.origin.x, 
						   contentFrame.origin.y + contentFrame.size.height,
						   pickerSize.width,
						   pickerSize.height);
	}

	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
	taskViewController.categoryPicker.frame = frame;
	[UIView commitAnimations];
	categoryPickerHidden = !categoryPickerHidden;
}

- (void)createStartMenu {
	if (startMenuView == nil) {
		int menuHeight = 250;
		CGRect frame = [taskViewController.view bounds];
		frame.origin.x = -180;
		frame.origin.y = frame.origin.y + frame.size.height - menuHeight - TOOLBAR_HEIGHT + 1;
		frame.size.width = 180;
		frame.size.height = menuHeight;
		startMenuView = [[StartMenuView alloc] initWithFrame:frame];
		
		startMenuView.navigationController = taskViewController.navigationController;
		startMenuView.toolbarItemsDelegate = self;
		[taskViewController.view addSubview:startMenuView];
	}
}

- (void)hideStartMenu {
	CGRect frame = startMenuView.frame;
	frame.origin.x = -frame.size.width;
	startMenuView.frame = frame;
	startMenuHidden = YES;
}

- (void)viewMoreScreen:(id)sender {
	[self createStartMenu];
	CGRect frame = startMenuView.frame;
	if (startMenuHidden) {
		frame.origin.x = 0;
	} else {
		frame.origin.x = -frame.size.width;
	}
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.1];
	startMenuView.frame = frame;
	[UIView commitAnimations];
	startMenuHidden = !startMenuHidden;
}

- (void)actionAddTask:(id)sender {
	DetailViewController *controller = [[DetailViewController alloc] init];
	[controller setTaskViewController:taskViewController];
	controller.title = @"New Task";
	if (taskViewController.parentTask) {
		[controller setParentTask:taskViewController.parentTask];
	}
	[controller loadView];
	[taskViewController.navigationController pushViewController:controller animated:YES];
	[controller release];	
}

- (void)viewTodayView:(id)sender {
	TodayViewController *controller = [[TodayViewController alloc] init];
	[controller loadView];
    controller.title = @"Today";
	[taskViewController.navigationController pushViewController:controller animated:YES];
	[controller release];
}
@end
