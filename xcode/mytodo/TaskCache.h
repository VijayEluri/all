
#import <Foundation/Foundation.h>

@class TaskData;

@interface TaskCache : NSObject {
	NSMutableArray *keyArray;
	NSMutableDictionary *cacheDict;
}

+ (TaskCache *)sharedInstance;
- (TaskData *)taskDataWithId:(int)taskId;
- (TaskData *)taskDataWithIdOrNil:(int)taskId;
- (void)setTaskData:(TaskData *)data key:(int)taskId;

@end
