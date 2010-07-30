
#import "Categories.h"
#import "Category.h"
#import "DbManager.h"

@implementation Categories

static BOOL allocFromClassMethod = NO;
static Categories *categories = nil;

-(Categories *) init {
	if (!allocFromClassMethod) {
		NSLog(@"You should never call Categories.alloc directly!");
		NSAssert(FALSE, @"You should never call DbManager.alloc directly!");
		return nil;
	}
	[super init];
    selectedCategoryId = ALL_CATEGORY;
	categoriesArray = [[NSMutableArray alloc] init];
    allocFromClassMethod = NO;
	return self;
}

- (NSUInteger)retainCount {
	return NSUIntegerMax;
}

- (oneway void)release {
}

- (id)retain {
	return categories;
}

- (id)autorelease {
	return categories;
}

-(void)dealloc {
	[categoriesArray release];
	[super dealloc];
}

- (Category *)categoryAtIndex: (int)value {
	return [categoriesArray objectAtIndex:value];
}

- (void)addCategory: (Category *)value {
	[categoriesArray addObject:value];
}

- (void)removeCategory: (Category *)value {
    [categoriesArray removeObject:value];
}

- (int)count {
	return categoriesArray.count;
}

- (void)selectCategory:(int)categoryId {
    selectedCategoryId = categoryId;
}

- (int)selectedCategoryId {
	return selectedCategoryId;
}

- (NSString *)queryString {
    if (selectedCategoryId == ALL_CATEGORY) {
        return @"";
    }
    return [NSString stringWithFormat:@" AND category_id = %d", selectedCategoryId];
}

+ (Categories *)allCategories {
    static sqlite3_stmt *select_statement = nil;
	static NSString *sql = @"SELECT category_id FROM category";
    
    if (categories != nil) return categories;
    
    allocFromClassMethod = YES;
    categories = [[Categories alloc] init];
	
	DbManager *instance = [DbManager sharedInstance];
	[instance prepareStatement:&select_statement sql:sql];
	while (sqlite3_step(select_statement) == SQLITE_ROW) {
		int primaryKey = sqlite3_column_int(select_statement, 0);
		Category *category = [[Category alloc] initWithId:primaryKey];
		[categories addCategory:category];
		[category release];
	}
	sqlite3_reset(select_statement);
	
	return categories;
}

@end
