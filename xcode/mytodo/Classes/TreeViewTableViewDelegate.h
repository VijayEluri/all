
#import <Foundation/Foundation.h>

@class TreeViewController;
@class Task;

@interface TreeViewTableViewDelegate : NSObject <UITableViewDelegate, UIActionSheetDelegate> {
    TreeViewController *treeViewController;
    Task *parentTask;
}

@property (retain) TreeViewController *treeViewController;

@end
