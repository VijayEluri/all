
#import <UIKit/UIKit.h>

#define CELL_TEXT_VIEW_ID @"CellTextViewId"

@interface CellTextView : UITableViewCell
{
    UITextView *textView;
}

- (void)setView:(UITextView *)inView;

@end
