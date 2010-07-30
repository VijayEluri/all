
#import <Foundation/Foundation.h>

@class TodayViewController;

@interface TodayViewTableViewDelegate : NSObject <UITableViewDelegate> {
	TodayViewController *todayViewController;
}

@property (assign) TodayViewController *todayViewController;

@end
