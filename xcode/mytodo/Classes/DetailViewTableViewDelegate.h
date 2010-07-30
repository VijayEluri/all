
#import <Foundation/Foundation.h>

@class DetailViewController;

@interface DetailViewTableViewDelegate : NSObject <UITableViewDelegate> {
    DetailViewController *detailViewController;
}

@property (assign) DetailViewController *detailViewController;

@end
