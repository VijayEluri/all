
#import <Foundation/Foundation.h>

@class Categories;
@class DetailViewController;
@class Task;

@interface DetailViewTableViewDataSource : NSObject <UITableViewDataSource, UITextViewDelegate> {
    DetailViewController *detailViewController;
	Categories *categories;
    Task *task;
	Task *parentTask;
}

@property (assign) DetailViewController *detailViewController;
@property (retain) Task *task;
@property (retain) Task *parentTask;

- (DetailViewTableViewDataSource *)init;
- (int)selectedCategoryId:(UITableView *)tableView;

@end
