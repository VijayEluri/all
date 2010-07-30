
#import "TaskViewGeneralDelegate.h"
#import "TaskViewController.h"

@implementation TaskViewGeneralDelegate

-(id)init:(TaskViewController *)controller {
	[super init];
	taskViewController = controller;
	return self;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    [super dealloc];
}

@end
