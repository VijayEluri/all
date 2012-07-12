
#import <Foundation/Foundation.h>

@class DetailViewController;

@interface DetailViewTableViewDelegate : NSObject <UITableViewDelegate> {
    DetailViewController *detailViewController;
}

@property (retain) DetailViewController *detailViewController;

@end
