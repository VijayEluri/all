
#import "DetailViewTableViewDelegate.h"
#import "DetailViewController.h"

@implementation DetailViewTableViewDelegate

@synthesize detailViewController;

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[super dealloc];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	if ((indexPath.section == CATEGORY_SECTION) && !detailViewController.readOnly) {
		UITableViewCell *cell;    
		int rows = [tableView numberOfRowsInSection:indexPath.section];
		for (int i = 0; i < rows; ++i) {
			NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:i inSection:indexPath.section];
			cell = [tableView cellForRowAtIndexPath:newIndexPath];
			cell.accessoryType = UITableViewCellAccessoryNone;
		}
		
		cell = [tableView cellForRowAtIndexPath:indexPath];
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
	}
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	switch (indexPath.section) {
		case CONTENT_SECTION:
			switch (indexPath.row) {
				case 0:
					return 100;
				default:
					return 45;
			}
			break;
		case CATEGORY_SECTION:
			return 40;
	}
	return 0;
}


@end
