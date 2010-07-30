
#import "MyButton.h"


@implementation MyButton

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[super dealloc];
}

@end
