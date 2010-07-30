
#import <Foundation/Foundation.h>

@interface Category : NSObject {
	BOOL loaded;
	int categoryId;
	NSString *categoryName;
}

-(Category *)initWithId:(int)value;

-(int)categoryId;
-(void)setCategoryId:(int)value;

-(NSString *)categoryName;
-(void)setCategoryName:(NSString *)value;

-(void)saveToDb;
-(void)removeFromDb;

+(void)createCategory:(Category *)category;

@end
