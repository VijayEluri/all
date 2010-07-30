
#import <Foundation/Foundation.h>

@class Category;

enum tagSpecialCategory {
    ALL_CATEGORY = -2,
    NO_CATEGORY = -1
};

@interface Categories : NSObject {
    int selectedCategoryId;
	NSMutableArray *categoriesArray;
}

- (Category *)categoryAtIndex: (int)value;
- (void)addCategory: (Category *)value;
- (void)removeCategory: (Category *)value;
- (int)count;
- (void)selectCategory:(int)categoryId;
- (int)selectedCategoryId;
- (NSString *)queryString;

+ (Categories *)allCategories;

@end
