
#import <Foundation/Foundation.h>

#define TODAY_VIEW_CELL_ID @"TodayViewCellId"

@class Task;
@class TodayViewController;

@interface TodayViewCell : UITableViewCell {
	UIButton *completionCheckButton;
	UILabel *taskContentLabel;
	UILabel *subTaskCountLabel;
	
	Task *task;
	TodayViewController *todayViewController;
}

@property (retain) Task *task;
@property (retain) TodayViewController *todayViewController;

- (void)onAccessoryClick;

@end
