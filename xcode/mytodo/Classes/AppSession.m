
#import "AppSession.h"
#import "TaskViewController.h"
#import "DetailViewController.h"
#import "LogBookViewController.h"
#import "TodayViewController.h"
#import "Tasks.h"
#import "Task.h"

#define LOG_BOOK_KEY @"LogBook"
#define TASK_VIEW_KEY @"TaskView"
#define DETAIL_VIEW_KEY @"DetailView"
#define TODAY_VIEW_KEY @"TodayView"

@implementation AppSession

- (NSString *)writeableRestoreDataPath {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
	NSString *path = [documentsDirectory stringByAppendingPathComponent:@"RestoreData.plist"];
	return path;
}

#pragma mark restore view

- (void)restoreLogBook {
	LogBookViewController *controller = [[LogBookViewController alloc]init];
	[controller loadView];
	controller.title = @"Log Book";
	[navigationController pushViewController:controller animated:NO];
	[controller release];
}

- (void)restoreTodayView {
	TodayViewController *controller = [[TodayViewController alloc] init];
	[controller loadView];
	[navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)restoreTaskView:(NSDictionary *)dict {
	NSMutableDictionary *taskDict = [NSMutableDictionary dictionaryWithDictionary:[dict objectForKey:@"TaskView"]];
	int parentId = [[taskDict objectForKey:@"ParentTaskId"] intValue];
	//NSString *state = [taskDict objectForKey:@"State"];
	
	if (parentId > 0) {
		Tasks *tasksToRestore = [[Tasks alloc] init];
		Task *task = [[Task alloc] initWithId:parentId];
		
		Task *tempTask = task;
		do {
			[tasksToRestore addTask:tempTask];
			tempTask = tempTask.parentTask;
		} while (tempTask != nil);
		[task release];
		
		for (int i = tasksToRestore.count - 1; i >= 0; i--) {
			task = [tasksToRestore taskAtIndex:i];
			TaskViewController *controller = [[TaskViewController alloc] initWithNibName:@"TaskViewController" bundle:nil];
			[controller setParentTask:task];
			[navigationController pushViewController:controller animated:NO];
			[controller release];
		}
		[tasksToRestore release];
	}
}

- (void)restoreFromSavedData {
	NSString *errorDesc = nil;
	NSPropertyListFormat format;
	NSString *plistPath = [self writeableRestoreDataPath];
	NSData *plistXML = [[NSFileManager defaultManager] contentsAtPath:plistPath];
	NSDictionary *temp = (NSDictionary *)[NSPropertyListSerialization
										  propertyListFromData:plistXML
										  mutabilityOption:NSPropertyListMutableContainersAndLeaves
										  format:&format errorDescription:&errorDesc];
	if (!temp) {
		NSLog(@"Error: %s", errorDesc);
		[errorDesc release];
	}
	
	NSString *currentView = [temp objectForKey:@"Screen"];
	if (currentView == nil) {
		return;
	}
	
	if ([currentView isEqualToString:LOG_BOOK_KEY]) {
		[self restoreLogBook];
	} else 
	if ([currentView isEqualToString:TASK_VIEW_KEY]) {
		[self restoreTaskView:temp];
	} else
	if ([currentView isEqualToString:TODAY_VIEW_KEY]) {
		[self restoreTodayView];
	}
}

# pragma mark save view

- (void)saveDataForRestoring {
	UIViewController *activeController = [navigationController visibleViewController];
	NSString *currentView = nil;
	NSString *errorDesc;
	
	NSString *bundlePath = [self writeableRestoreDataPath];
	NSMutableDictionary *plistDict = [[NSMutableDictionary alloc] init];

	if ([activeController isKindOfClass:[TodayViewController class]]) {
		currentView = TODAY_VIEW_KEY;
	} else 
	if ([activeController isKindOfClass:[LogBookViewController class]]) {
		currentView = LOG_BOOK_KEY;
	} else 
	if ([activeController isKindOfClass:[TaskViewController class]]) {
		currentView = TASK_VIEW_KEY;
		TaskViewController *taskViewController = (TaskViewController *) activeController;
		int parentId = -1;
		if (taskViewController.parentTask) {
			parentId = taskViewController.parentTask.taskId;
		}
		NSString *state = @"Normal";
		if (taskViewController.tableView.editing) {
			state = @"Editing";
		}
		
		NSMutableDictionary *taskDict = [[NSMutableDictionary alloc] init];
		[taskDict setObject:[NSNumber numberWithInt:parentId] forKey:@"ParentTaskId"];
		[taskDict setObject:state forKey:@"State"];
		[plistDict setObject:taskDict forKey:TASK_VIEW_KEY];
		[taskDict release];
	} else 
	if ([activeController isKindOfClass:[DetailViewController class]]) {
		currentView = TASK_VIEW_KEY;
		DetailViewController *detailViewController = (DetailViewController *) activeController;
		NSMutableDictionary *taskDict = [[NSMutableDictionary alloc] init];
		Task *task = detailViewController.task;
		if (task) {
			[detailViewController saveCurrentTask];
		} else {
			[detailViewController createNewTask];
		}
		int parentId = detailViewController.taskViewController.parentTask.taskId;
		[taskDict setObject:[NSNumber numberWithInt:parentId] forKey:@"ParentTaskId"];
		[plistDict setObject:taskDict forKey:TASK_VIEW_KEY];
		[taskDict release];
	}
	
	if (currentView) {
		[plistDict setObject:currentView forKey:@"Screen"];
		
		NSData *plistData = [NSPropertyListSerialization dataFromPropertyList:plistDict
																	   format:NSPropertyListXMLFormat_v1_0
															 errorDescription:&errorDesc];
		if (plistData) {
			[plistData writeToFile:bundlePath atomically:YES];
		}	
	}
	[plistDict release];
}

- (AppSession *)init:(UINavigationController *)controller {
	if (self = [super init]) {
		navigationController = controller;
		return self;
	}
	return nil;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    [super dealloc];
}

- (void)saveState {
	[self saveDataForRestoring];
}

- (void)restoreState {
	[self restoreFromSavedData];
}

@end
