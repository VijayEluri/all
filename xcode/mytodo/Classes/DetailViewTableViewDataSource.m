
#import "DetailViewTableViewDataSource.h"
#import "DetailViewController.h"
#import "DetailViewTextViewDelegate.h"
#import "CellTextView.h"
#import "CellViewWithLabel.h"
#import "Category.h"
#import "Categories.h"
#import "Task.h"

@implementation DetailViewTableViewDataSource

@synthesize detailViewController;
@synthesize task;
@synthesize parentTask;

-(DetailViewTableViewDataSource *)init {
    self = [super init];
    categories = [Categories allCategories];
    return self;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return LAST_NIL_SECTION;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	switch (section) {
		case CONTENT_SECTION:
			return 3;
		case CATEGORY_SECTION:
			return [categories count] + 1;
	}
	return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	UITableViewCell *cell = nil;
	
	switch (indexPath.section) {
		case CONTENT_SECTION:
			switch (indexPath.row) {
				case 0:
					cell = [[CellTextView alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CELL_TEXT_VIEW_ID];
					[(CellTextView *) cell setView:detailViewController.contentTextView];
					break;
				case 1:
					cell = [[CellViewWithLabel alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CELL_VIEW_WITH_LABEL_ID];
					((CellViewWithLabel *)cell).nameLabel.text = @"Priority";
					((CellViewWithLabel *)cell).view = detailViewController.prioritySegment;
					break;
				case 2:
					cell = [[CellViewWithLabel alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CELL_VIEW_WITH_LABEL_ID];
					((CellViewWithLabel *)cell).nameLabel.text = @"Due in";
					((CellViewWithLabel *)cell).view = detailViewController.dueDateView;
					break;
			}
			break;
		case CATEGORY_SECTION:
			cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CategoryCell"];
			if (indexPath.row == 0) {
				cell.textLabel.text = @"<No Category>";
				if (task == nil) {
					if ((parentTask == nil) || (parentTask.categoryId < 0)) {
						cell.accessoryType = UITableViewCellAccessoryCheckmark;
					}
				} else if (task.categoryId < 0) {
					cell.accessoryType = UITableViewCellAccessoryCheckmark;
				}
			} else {
				Category *category = [categories categoryAtIndex:indexPath.row - 1];
				cell.textLabel.text = category.categoryName;
				if (task == nil) {
					if (category.categoryId == parentTask.categoryId) {
						cell.accessoryType = UITableViewCellAccessoryCheckmark;
					}
				} else if (category.categoryId == task.categoryId) {
					cell.accessoryType = UITableViewCellAccessoryCheckmark;
				}
			}
			break;
	}
	
	return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	switch (section) {
		case CONTENT_SECTION:
			return nil;
		case CATEGORY_SECTION:
			return @"Category";
	}
	return @"";
}

- (int)selectedCategoryId:(UITableView *)tableView {
    int categoryId = -1;

    int rows = [tableView numberOfRowsInSection:CATEGORY_SECTION];
    for (int i = 0; i < rows; ++i) {
        NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:i inSection:CATEGORY_SECTION];
        UITableViewCell *cell = [tableView cellForRowAtIndexPath:newIndexPath];
        if (cell.accessoryType == UITableViewCellAccessoryCheckmark) {
            if (i > 0) {
                Category *category = [categories categoryAtIndex:i - 1];
                categoryId = category.categoryId;
            }
            break;
        }
    }

    return categoryId;
}


@end
