
#import <UIKit/UIKit.h>

#define LOG_BOOK_CELL_ID @"LogBookCellId"

@class Task;

@interface LogBookCell : UITableViewCell {
	UIImageView *tickImageView;
	UILabel *taskContentLabel;
	UILabel *completionDateLabel;
	
	Task *theTask;

    int cellIndex;
}

@property (retain) Task *task;

@end
