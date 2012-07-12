
#import "TaskCache.h"
#import "Task.h"
@implementation TaskCache

static BOOL allocFromClassMethod = NO;
static TaskCache *taskCache = nil;

#define CACHE_SIZE 100

- (TaskCache *) init {
	if (!allocFromClassMethod) {
		NSLog(@"You should never call TaskCache.alloc directly!");
		NSAssert(FALSE, @"You should never call TaskCache.alloc directly!");
		return nil;
	}
	
	if (self = [super init]) {
		keyArray = [[NSMutableArray alloc] initWithCapacity:CACHE_SIZE];
		cacheDict = [[NSMutableDictionary alloc] initWithCapacity:CACHE_SIZE];
		allocFromClassMethod = NO;
		return self;
	}
	return nil;
}

+ (TaskCache *)sharedInstance {
	if (taskCache == nil) {
		allocFromClassMethod = YES;
		taskCache = [[TaskCache alloc] init];
	}
	return taskCache;
}

- (TaskData *)taskDataWithIdOrNil:(int)taskId {
	NSString *key = [NSString stringWithFormat:@"%d", taskId];
	return [cacheDict valueForKey:key];
}

- (TaskData *)taskDataWithId:(int)taskId {
	NSString *key = [NSString stringWithFormat:@"%d", taskId];
	TaskData *data = [cacheDict valueForKey:key];
	if (data != nil) {
		NSLog(@"Found %@ with key %@.", data, key);
		return data;
	}
	data = [[TaskData alloc] init];
	data.taskId = taskId;
	
	if (keyArray.count == CACHE_SIZE) {
		NSString *keyToRemove = [keyArray objectAtIndex:0];
		NSLog(@"Remove from cache of key %@.", keyToRemove);
		[cacheDict removeObjectForKey:keyToRemove];
		[keyArray removeObjectAtIndex:0];
	}
	[keyArray addObject:key];
	[cacheDict setValue:data forKey:key];
	
	return data;
}

- (void)setTaskData:(TaskData *)data key:(int)taskId {
	NSString *key = [NSString stringWithFormat:@"%d", taskId];
	TaskData *td = [cacheDict valueForKey:key];
	if (td == nil) {
		[keyArray addObject:key];
		NSLog(@"Adding to cache of key %@.", key);
	} else {
		NSLog(@"Updating cache of key %@.", key);
	}
	[cacheDict setValue:data forKey:key];
}

@end
