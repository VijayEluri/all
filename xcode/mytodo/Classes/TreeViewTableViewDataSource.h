
#import <Foundation/Foundation.h>

@class TreeViewController;
@class Tasks;
@class Task;

@interface TreeViewTableViewDataSource : NSObject <UITableViewDataSource> {
    TreeViewController *treeViewController;
    NSMutableArray *indentLevelArray;
    Tasks *tasks;
}

@property (retain) TreeViewController *treeViewController;

- (TreeViewTableViewDataSource *)init;
- (Task *)taskAt:(NSIndexPath *)path;
- (void)openSubTasksOf:(Task *)task withIn:(UITableView *)tv;
- (void)closeSubTasksOf:(Task *)task withIn:(UITableView *)tv;

@end
