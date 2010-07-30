
#import <Foundation/Foundation.h>

@class DetailViewController;

@interface DetailViewTextViewDelegate : NSObject <UITextViewDelegate> {
	DetailViewController *controller;
}

- (id)init:(DetailViewController *)value;
- (void)createButton;

@end
