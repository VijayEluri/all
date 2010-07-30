
#import "DetailViewTextViewDelegate.h"
#import "DetailViewController.h"

@implementation DetailViewTextViewDelegate

- (id)init:(DetailViewController *)value {
    [super init];
    controller = value;
    return self;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[super dealloc];
}

- (void)createButton {
	NSLog(@"rightBarButtonItem: %@", controller.navigationItem.rightBarButtonItem);
	if (controller.navigationItem.rightBarButtonItem == nil) {
		UIBarButtonItem* doneItem = [[UIBarButtonItem alloc] 
									 initWithBarButtonSystemItem:UIBarButtonSystemItemDone
									 target:self 
									 action:@selector(doneTextViewEditingAction:)];
		controller.navigationItem.rightBarButtonItem = doneItem;
		[doneItem release];
	}
}

- (BOOL)textViewShouldBeginEditing:(UITextView *)textView {
	NSLog(@"textViewShouldBeginEditing");
	[controller hideAllPicker];
	[self createButton];
	return YES;
}

- (void)doneTextViewEditingAction:(id)sender {
	NSLog(@"doneTextViewEditingAction");
	[controller.contentTextView resignFirstResponder];
	controller.navigationItem.rightBarButtonItem = nil;
}

@end
