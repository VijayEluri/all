
#import "AppGlobal.h"


@implementation AppGlobal

static NSDateFormatter *dateTimeFormatter = nil;
+ (NSDateFormatter *)dateTimeFormatter {
    if (dateTimeFormatter == nil) {
        dateTimeFormatter = [[NSDateFormatter alloc] init];
        [dateTimeFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    }
    return dateTimeFormatter;
}

static NSDateFormatter *dateFormatter = nil;
+ (NSDateFormatter *)dateFormatter {
    if (dateFormatter == nil) {
        dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd"];
    }
    return dateFormatter;
}

@end
