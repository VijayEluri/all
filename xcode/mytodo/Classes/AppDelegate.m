
#import "AppDelegate.h"
#import "TaskViewController.h"
#import "TaskViewCell.h"
#import "AppSession.h"
#import "Tasks.h"

@implementation AppDelegate

@synthesize window;
@synthesize navigationController;

#pragma mark startup

- (void)applicationDidFinishLaunching:(UIApplication *)application {
	NSLog(@"-- starting -- init AppSession");
	session = [[AppSession alloc] init:navigationController];

	NSLog(@"-- starting -- init root TaskViewController");	
	rootTaskViewController = [[TaskViewController alloc] init];
    [rootTaskViewController loadView];
	rootTaskViewController.title = @"My Todo";
	[navigationController initWithRootViewController:rootTaskViewController];
	
	[window addSubview:[navigationController view]];
	[window makeKeyAndVisible];
	
	NSLog(@"-- starting -- restore state");
	[session restoreState];
	NSLog(@"-- starting -- done.");
}

#pragma mark terminating

- (void)updateBadgeNumber:(UIApplication *)application {
	Tasks *tasks = [Tasks allocTasks:nil completed:NO];
	application.applicationIconBadgeNumber = [tasks count];
	[tasks release];
}

- (void)applicationWillTerminate:(UIApplication *)application {
	[session saveState];
	[self updateBadgeNumber:application];
}

- (void)dealloc {
	[session release];
	[rootTaskViewController release];
	[navigationController release];
	[window release];
	[super dealloc];
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    NSLog(@"NO MEMORY!");
}

@end
