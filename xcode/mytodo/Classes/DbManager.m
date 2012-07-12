
#import "DbManager.h"
#import "Consts.h"

@implementation DbManager

static BOOL allocFromClassMethod = NO;
static DbManager *dbManager = nil;

#pragma mark Private Methods

- (NSString *)writeableDBPath {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
	NSString *path = [documentsDirectory stringByAppendingPathComponent:DB_NAME];
	return path;
}

- (NSString *)defaultDbPath {
	NSString *defaultDBPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:DB_NAME];
	return defaultDBPath;
}

// Creates a writable copy of the bundled default database in the application Documents directory.
- (void)createEditableCopyOfDatabaseIfNeeded {

    BOOL success;
	
	NSError *error;
	NSFileManager *fileManager = [NSFileManager defaultManager];
	
	NSString *writableDBPath = [self writeableDBPath];
	
    success = [fileManager fileExistsAtPath:writableDBPath];
    
	// for dev only:
	// always copy db to overwrite

//#define DEV_OVERWRITE_DB
	
#ifdef DEV_OVERWRITE_DB
	if (success) {
		[fileManager removeItemAtPath:writableDBPath error:&error];
	}
#else
	if (success) return;
#endif
    
	NSString *defaultDBPath;
	defaultDBPath = [self defaultDbPath];
    success = [fileManager copyItemAtPath:defaultDBPath toPath:writableDBPath error:&error];
	
    if (!success) {
        NSAssert1(0, @"Failed to create writable database file with message '%@'.", [error localizedDescription]);
    }
}

- (void) openDatabase {
	if (sqliteDatabase == nil) {
		NSString *dbPath = [self writeableDBPath];
		if (sqlite3_open([dbPath UTF8String], &sqliteDatabase) == SQLITE_OK) {
		} else {
			sqlite3_close(sqliteDatabase);
			NSAssert1(0, @"Failed to open database with message '%s'.", sqlite3_errmsg(sqliteDatabase));
		}
	}
}

- (void) closeDatabase {
	if (sqliteDatabase) {
		sqlite3_close(sqliteDatabase);
	}
}

- (DbManager *) init {
	if (!allocFromClassMethod) {
		NSLog(@"You should never call DbManager.alloc directly!");
		NSAssert(FALSE, @"You should never call DbManager.alloc directly!");
		return nil;
	}
	
	if (self = [super init]) {
		[self createEditableCopyOfDatabaseIfNeeded];
		[self openDatabase];
	}
    allocFromClassMethod = NO;
	return self;
}

- (void)dealloc {
	[self closeDatabase];
}

#pragma mark Public Methods

+ (DbManager *)sharedInstance {
	if (dbManager == nil) {
		allocFromClassMethod = YES;
		dbManager = [[DbManager alloc] init];
		[dbManager openDatabase];
	}
	return dbManager;
}

- (int)lastInsertRowId {
	return sqlite3_last_insert_rowid(sqliteDatabase);
}

- (void)prepareStatement: (sqlite3_stmt **)statement sql:(NSString *)sql {
	if (*statement == nil) {
		int success = sqlite3_prepare_v2(sqliteDatabase, [sql UTF8String], -1, statement, NULL);
		NSAssert1((success == SQLITE_OK), @"Error: failed on sqlite with message '%s'.", sqlite3_errmsg(sqliteDatabase));	
	}
}

- (void)checkResult:(int)result equals:(int)target {
	NSAssert1((result == target), @"Error: failed on sqlite with message '%s'.", sqlite3_errmsg(sqliteDatabase));	
}

@end
