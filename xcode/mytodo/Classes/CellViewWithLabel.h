
#import <UIKit/UIKit.h>


#define CELL_VIEW_WITH_LABEL_ID @"CellViewWithLabelId"

@interface CellViewWithLabel : UITableViewCell
{
	UILabel	*nameLabel;
	UIView	*view;
}

@property (retain) UIView *view;
@property (retain) UILabel *nameLabel;

- (void)setView:(UIView *)inView;

@end
