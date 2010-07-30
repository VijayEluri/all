
#import <Foundation/Foundation.h>

@class TaskViewController;

@interface TaskViewGeneralDelegate : NSObject {
	TaskViewController *taskViewController;
}

-(id)init:(TaskViewController *)controller;

@end
