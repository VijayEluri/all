
#import <Foundation/Foundation.h>

@class TodayViewController;

@interface TodayViewTableViewDelegate : NSObject <UITableViewDelegate> {
	TodayViewController *todayViewController;
}

@property (retain) TodayViewController *todayViewController;

@end
