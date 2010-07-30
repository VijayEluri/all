
#import <UIKit/UIKit.h>


#define CELL_VIEW_WITH_LABEL_ID @"CellViewWithLabelId"

@interface CellViewWithLabel : UITableViewCell
{
	UILabel	*nameLabel;
	UIView	*view;
}

@property (nonatomic, retain) UIView *view;
@property (nonatomic, retain) UILabel *nameLabel;

- (void)setView:(UIView *)inView;

@end
