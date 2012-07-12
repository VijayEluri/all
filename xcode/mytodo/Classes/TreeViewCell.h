
#import <UIKit/UIKit.h>

#define TREE_VIEW_CELL_ID @"TreeViewCellId"

@class Task;
@class TreeViewController;

@interface TreeViewCell : UITableViewCell {
	UIButton *treeNodeButton;
	UILabel *taskContentLabel;
	UILabel *subTaskCountLabel;

    int indentLevel;
    Task *task;
    TreeViewController *treeViewController;
}

@property int indentLevel;
@property (retain) Task *task;
@property (retain) TreeViewController *treeViewController;

@end
