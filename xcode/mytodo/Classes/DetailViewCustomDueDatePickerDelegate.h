
#import <Foundation/Foundation.h>

@class DetailViewController;

@interface DetailViewCustomDueDatePickerDelegate : NSObject <UIPickerViewDelegate> {
	DetailViewController *detailViewController;
}

- (DetailViewCustomDueDatePickerDelegate *)init:(DetailViewController *)controller;
- (NSInteger)rowOfDate:(NSString *)date;

@end
