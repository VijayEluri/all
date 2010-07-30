
#import "TaskViewCategoryPickerDelegate.h"
#import "TaskViewController.h"
#import "Categories.h"
#import "Category.h"

@implementation TaskViewCategoryPickerDelegate

- (id)init:(TaskViewController *)controller {
	[super init:controller];
	categories = [Categories allCategories];
	return self;
}

- (void)dealloc {
	[super dealloc];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
	if (row == 0) {
		[categories selectCategory:ALL_CATEGORY];
	} else if (row == 1) {
		[categories selectCategory:NO_CATEGORY];
	} else {
        Category *category = [categories categoryAtIndex:row - 2];
		[categories selectCategory:category.categoryId];
	}
	[taskViewController hideCategoryPicker];
	[taskViewController updateCategoryButtonTitle];
	[taskViewController reloadDataFromDb];
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    if (row == 0) {
        return @"<All Tasks>";
    } else if (row == 1) {
        return @"<No Category>";
    } else {
        Category *category = [categories categoryAtIndex:row - 2];
        return [category categoryName];
    }
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component {
	return 280.0;
}

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component {
	return 40.0;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
	return [categories count] + 2;
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
	return 1;
}

@end
