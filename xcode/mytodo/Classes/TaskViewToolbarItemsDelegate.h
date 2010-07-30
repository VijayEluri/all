
#import <Foundation/Foundation.h>
#import "TaskViewGeneralDelegate.h"

@class StartMenuView;

@interface TaskViewToolbarItemsDelegate : TaskViewGeneralDelegate {
	BOOL categoryPickerHidden;
	BOOL startMenuHidden;
	StartMenuView *startMenuView;
}

- (void)showCategoryPicker:(id)sender;
- (void)hideStartMenu;

@end
