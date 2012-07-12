
#import "TaskViewGeneralDelegate.h"
#import "TaskViewController.h"

@implementation TaskViewGeneralDelegate

-(id)init:(TaskViewController *)controller {
	self = [super init];
	taskViewController = controller;
	return self;
}

@end
