#import <sqlite3.h>
#import "Category.h"
#import "DbManager.h"

@implementation Category

-(Category *)initWithId:(int)value {
	[super init];
	categoryId = value;
	loaded = NO;
	return self;
}

-(void)dealloc {
	NSLog(@"dealloc %@", self);
	[categoryName release];
	[super dealloc];
}

- (void)loadFromDb {
    static sqlite3_stmt *select_statement = nil;

	if (loaded) return;
	if (categoryId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"SELECT category_name FROM category WHERE category_id=?";
	[instance prepareStatement:&select_statement sql:sql];
	
	sqlite3_bind_int(select_statement, 1, categoryId);
	
	if (sqlite3_step(select_statement) == SQLITE_ROW) {
		char *str = (char *)sqlite3_column_text(select_statement, 0);
		self.categoryName = (str) ? [NSString stringWithUTF8String:str] : @"";
	}
	
	sqlite3_reset(select_statement);
	
	loaded = YES;
}

-(int)categoryId {
	return categoryId;
}

-(void)setCategoryId:(int)value {
	NSAssert((categoryId <= 0), @"Error: cannot set a category's ID when it has one");
	categoryId = value;
}

-(NSString *)categoryName {
	if (!loaded) [self loadFromDb];
	return categoryName;
}

-(void)setCategoryName:(NSString *)value {
	[categoryName release];
	categoryName = [value retain];
}

-(void)saveToDb {
    static sqlite3_stmt *update_statement = nil;
    
	if (categoryId <= 0) return;
	
	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"UPDATE category SET category_name = ? WHERE category_id = ?";
	[instance prepareStatement:&update_statement sql:sql];
    
	sqlite3_bind_text(update_statement, 1, [categoryName UTF8String], -1, SQLITE_TRANSIENT);
	sqlite3_bind_int(update_statement, 2, categoryId);
	
	int success = sqlite3_step(update_statement);
	
	sqlite3_reset(update_statement);
    
	[instance checkResult:success equals:SQLITE_DONE];	
}

-(void)removeFromDb {
    static sqlite3_stmt *delete_statement = nil;
	if (categoryId <= 0) return;
    
	DbManager *instance = [DbManager sharedInstance];
    
	static NSString *sqlSub = @"DELETE FROM category WHERE category_id = ?";
    
	[instance prepareStatement:&delete_statement sql:sqlSub];
	
	sqlite3_bind_int(delete_statement, 1, categoryId);
	int success = sqlite3_step(delete_statement);
	
	sqlite3_reset(delete_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];	
}

+(void)createCategory:(Category *)category {
    static sqlite3_stmt *insert_statement = nil;

	DbManager *instance = [DbManager sharedInstance];
	
	static NSString *sql = @"INSERT INTO category VALUES(NULL, ?, '')";
	[instance prepareStatement:&insert_statement sql:sql];
	
	sqlite3_bind_text(insert_statement, 1, [category.categoryName UTF8String], -1, SQLITE_TRANSIENT);
	
	int success = sqlite3_step(insert_statement);
	
	sqlite3_reset(insert_statement);
	
	[instance checkResult:success equals:SQLITE_DONE];
	
	category.categoryId = [instance lastInsertRowId];
}

@end
