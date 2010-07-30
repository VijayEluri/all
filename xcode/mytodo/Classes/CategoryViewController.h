
#import <UIKit/UIKit.h>

@class Categories;

@interface CategoryViewController : UIViewController <UITableViewDataSource, 
                                                      UITableViewDelegate, 
                                                      UITextFieldDelegate> 
{
    Categories *categories;
    UITableView	*tableView;
    UIView *contentView;
    
    UIView *detailView;
    UITextField *categoryNameField;
    UILabel *categoryNameLabel;
    
    UIToolbar *toolbar;
    
    int action;
    NSIndexPath *currentEditingIndexPath;
}

@end
