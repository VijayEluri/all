
#import <Foundation/Foundation.h>
#import "TaskViewGeneralDelegate.h"

@class Categories;

@interface TaskViewCategoryPickerDelegate : TaskViewGeneralDelegate <UIPickerViewDelegate> {
	Categories *categories;
}

@end
