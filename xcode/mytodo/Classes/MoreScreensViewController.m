
#import "MoreScreensViewController.h"
#import "LogBookViewController.h"
#import "CategoryViewController.h"

@implementation MoreScreensViewController

- (void)showLogBook {
	LogBookViewController *controller = [[LogBookViewController alloc] init];
	[controller loadView];
	controller.title = @"Log Book";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)showCategories {
	CategoryViewController *controller = [[CategoryViewController alloc] init];
	[controller loadView];
	controller.title = @"Categories";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return @"Please select a view";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return 2;
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)aTableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellAccessoryDisclosureIndicator;
}

- (void)loadView {
	tableView = [[UITableView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] 
											 style:UITableViewStyleGrouped];	
	tableView.delegate = self;
	tableView.dataSource = self;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;

	self.view = tableView;
}

- (UITableViewCell *)tableView:(UITableView *)table cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	UITableViewCell *cell = [table dequeueReusableCellWithIdentifier:@"Cell"];
	
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"Cell"] autorelease];
	}
	
	switch(indexPath.row) {
		case 0:
			cell.textLabel.text = @"Log Book";
			break;
		case 1:
			cell.textLabel.text = @"Categories";
			break;
	}
	
	return cell;
}

- (void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	switch(indexPath.row) {
		case 0:
			[self showLogBook];
			break;
		case 1:
			[self showCategories];
			break;
	}
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
}

- (void)dealloc {
	[tableView release];
    [super dealloc];
}


@end
