
#import <Foundation/Foundation.h>
#import <sqlite3.h>

@interface DbManager : NSObject {
    sqlite3 *sqliteDatabase;
}

+ (DbManager *)sharedInstance;

- (void)prepareStatement:(sqlite3_stmt **)statement sql:(NSString *)sql; 
- (void)checkResult:(int)result equals:(int)target;
- (int)lastInsertRowId;

@end
