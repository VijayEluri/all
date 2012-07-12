#import "AppGlobal.h"
#import "DetailViewController.h"
#import "DetailViewCustomDueDatePickerDelegate.h"


@implementation DetailViewCustomDueDatePickerDelegate

static NSMutableArray *dateArray = nil;
static NSMutableArray *dateValueArray = nil;

- (DetailViewCustomDueDatePickerDelegate *)init:(DetailViewController *)controller; {
	self = [super init];
	detailViewController = controller;
	return self;
}

- (void)initArrays {
	const int secondsPerDay = 24 * 60 * 60;
	
	NSDateFormatter *formater = [AppGlobal dateFormatter];
	
	NSArray *weekDayNames = [formater weekdaySymbols];
	
	NSDate *now = [[NSDate alloc] init];
	NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
	NSDateComponents *weekComponents = [gregorian components:NSWeekdayCalendarUnit + NSDayCalendarUnit fromDate:now];
	
	dateArray = [[NSMutableArray alloc] initWithObjects: 
				 NO_DUE_DATE, 
				 @"Today", 
				 @"Tomorrow", 
				 @"This Week",  
				 @"Next Week", 
				 @"This Month", 
				 nil];
	
	NSRange dayRange = [gregorian rangeOfUnit:NSDayCalendarUnit inUnit:NSMonthCalendarUnit forDate:now];
	
	dateValueArray = [[NSMutableArray alloc] initWithObjects:
					  NO_DUE_DATE,
					  [formater stringFromDate:now],
					  [formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay]],
					  [formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay * (6 - [weekComponents weekday])]],
					  [formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay * (7 + 6 - [weekComponents weekday])]],
					  [formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay * (dayRange.length - [weekComponents day])]],
					  nil];
	
	for (int i = [weekComponents weekday]; i < [weekDayNames count]; ++i) {
		//[dateArray addObject:[NSString stringWithFormat:@"This %@", [weekDayNames objectAtIndex:i]]];
		[dateArray addObject:[weekDayNames objectAtIndex:i]];
		[dateValueArray addObject:[formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay * (i + 1 - [weekComponents weekday])]]];
	}
	
	for (int i = 1; i < [weekDayNames count]; ++i) {
		[dateArray addObject:[NSString stringWithFormat:@"Next %@", [weekDayNames objectAtIndex:i]]];
		[dateValueArray addObject:[formater stringFromDate:[now dateByAddingTimeInterval:secondsPerDay * (7 + 1 + i - [weekComponents weekday])]]];
	}
		
	for (int i = 0; i < [dateArray count]; ++i) {
		NSLog(@"%@, %@", [dateArray objectAtIndex:i], [dateValueArray objectAtIndex:i]);
	}
}

- (NSMutableArray *)dateArray {
	if (dateArray == nil) {
		[self initArrays];
	}
	return dateArray;
}

- (NSMutableArray *)dateValueArray {
	if (dateValueArray == nil) {
		[self initArrays];
	}
	return dateValueArray;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
	[detailViewController.dueDateButton setTitle:[self.dateValueArray objectAtIndex:row] forState:UIControlStateNormal];
	[detailViewController hideAllPicker];
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
	return [self.dateArray objectAtIndex:row];
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component {
	return 280.0;
}

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component {
	return 40.0;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
	return [self.dateArray count];
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
	return 1;
}

- (NSInteger)rowOfDate:(NSString *)date {
	NSMutableArray *array = self.dateValueArray;
	for (int i = 0; i < array.count; ++i) {
		if ([date compare:[array objectAtIndex:i]] == NSOrderedSame) {
			return i;
		}
	}
	return -1;
}

@end
