
#import "CategoryViewController.h"
#import "Category.h"
#import "Categories.h"
#import "Consts.h"

#define DETAIL_VIEW_HEIGHT 90

enum editActionCode {
    NO_ACTION = 0,
    NEW_ACTION,
    EDIT_ACTION
};

@implementation CategoryViewController

#pragma mark Show/Hide Detail View

- (void)showDetailView {
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
    CGRect frame = tableView.frame;
    frame.origin.y = DETAIL_VIEW_HEIGHT;
    tableView.frame = frame;
    frame = detailView.frame;
    frame.origin.y = 0;
    detailView.frame = frame;
    [UIView commitAnimations];
}

- (void)hideDetailView {
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
    CGRect frame = tableView.frame;
    frame.origin.y = 0;
    tableView.frame = frame;
    frame = detailView.frame;
    frame.origin.y = -DETAIL_VIEW_HEIGHT;
    detailView.frame = frame;
    [UIView commitAnimations];
}

#pragma mark UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tv {
	return 1;
}

- (NSInteger)tableView:(UITableView *)tv numberOfRowsInSection:(NSInteger)section {
	return [categories count];
}

- (UITableViewCell *)tableView:(UITableView *)tv cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MyCell"];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"MyCell"] autorelease];
    }
	
	cell.textLabel.text = [[categories categoryAtIndex:indexPath.row] categoryName];
	
	return cell;
}

- (NSString *)tableView:(UITableView *)tv titleForHeaderInSection:(NSInteger)section {
	return @"All Categories";
}

- (void)tableView:(UITableView *)tv commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        Category *category = [categories categoryAtIndex:indexPath.row];
        [category removeFromDb];
        [categories removeCategory:category];
		[tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }	
}

#pragma mark UITableViewDelegate

- (void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    action = EDIT_ACTION;
    Category *category = [categories categoryAtIndex:indexPath.row];
    categoryNameField.text = category.categoryName;
    currentEditingIndexPath = [indexPath retain];
    tableView.userInteractionEnabled = NO;
    [self showDetailView];
    [categoryNameField becomeFirstResponder];
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)tv accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellAccessoryNone;
}

#pragma mark navigationItem

- (void)createNavigationItem {
    self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

#pragma mark ToolbarItem

- (void)actionAddCategory:(id)sender {
    [self showDetailView];
    tableView.userInteractionEnabled = NO;
    categoryNameField.text = @"";
    [categoryNameField becomeFirstResponder];
    action = NEW_ACTION;
}

- (void)createToolbarItem {
	UIBarButtonItem *flexItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace
																			  target:nil
																			  action:nil];
	UIBarButtonItem* newItem = [[UIBarButtonItem alloc] 
								initWithBarButtonSystemItem:UIBarButtonSystemItemAdd
                                target:self 
                                action:@selector(actionAddCategory:)];
	NSArray *items = [NSArray arrayWithObjects: flexItem, newItem, nil];
	[toolbar setItems:items animated:NO];
	[newItem release];
    [flexItem release];
}

#pragma mark UITextFieldDelegate

- (void)newCategory:(NSString *)categoryName {
    Category *category = [[Category alloc] init];
    category.categoryName = categoryName;
    [Category createCategory:category];
    [categories addCategory:category];
    [category release];
    
    NSIndexPath *newIndexPath = [NSIndexPath indexPathForRow:[categories count] - 1 inSection:0];
    NSArray *insertIndexPaths = [NSArray arrayWithObjects:newIndexPath, nil];
    [tableView insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
}

- (void)updateCategory:(NSString *)categoryName {
    Category *category = [categories categoryAtIndex:currentEditingIndexPath.row];
    category.categoryName = categoryName;
    [category saveToDb];
    [tableView reloadData];
    [tableView deselectRowAtIndexPath:currentEditingIndexPath animated:YES];
    [currentEditingIndexPath release];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	NSString *categoryName = [categoryNameField.text 
							  stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if ([categoryName length] > 0) {
        if (action == NEW_ACTION) {
            [self newCategory:categoryName];
        } else if (action == EDIT_ACTION) {
            [self updateCategory:categoryName];
        }
    }
    [categoryNameField resignFirstResponder];
    [self hideDetailView];
    tableView.userInteractionEnabled = YES;
    return FALSE;
}

#pragma mark UIView

- (void)setEditing:(BOOL)editing animated:(BOOL)animated {
	[tableView setEditing:editing animated:animated];
	[super setEditing:editing animated:animated];
}

#pragma mark Others

// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)createTableView {
    CGRect frame = [contentView frame];
    frame.origin.y = 0;
    frame.size.height -= 2 * TOOLBAR_HEIGHT;
	tableView = [[UITableView alloc] initWithFrame:frame 
                                             style:UITableViewStylePlain];	
	tableView.delegate = self;
	tableView.dataSource = self;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	[contentView addSubview:tableView];
}

- (void)createDetailView {
    CGRect frame = [contentView frame];
    frame.origin.y = -DETAIL_VIEW_HEIGHT;
    frame.size.height = DETAIL_VIEW_HEIGHT;
    
    detailView = [[UIView alloc] initWithFrame:frame];
    
    frame = CGRectMake(10.0, 5.0, 300.0, 35.0);
    categoryNameLabel = [[UILabel alloc] initWithFrame:frame];
    categoryNameLabel.font = [UIFont systemFontOfSize:16.0];
    categoryNameLabel.text = @"Please Input Category Name Below:";

    frame = CGRectMake(10.0, 40.0, 300.0, 35.0);
    categoryNameField = [[UITextField alloc] initWithFrame:frame];
    categoryNameField.rightViewMode = UITextFieldViewModeAlways;
    categoryNameField.borderStyle = UITextBorderStyleRoundedRect;
    categoryNameField.clearButtonMode = UITextFieldViewModeAlways;
    categoryNameField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    categoryNameField.font = [UIFont systemFontOfSize:16.0];
    categoryNameField.returnKeyType = UIReturnKeyDone;
    categoryNameField.delegate = self;

    [detailView addSubview:categoryNameLabel];
    [detailView addSubview:categoryNameField];
    
    [contentView addSubview:detailView];
}

- (void)createToolbar {
	toolbar = [UIToolbar new];
	toolbar.barStyle = UIBarStyleDefault;
	
	[toolbar sizeToFit];
    CGRect frame = [contentView frame];
    frame.origin.y = frame.size.height - 2 * TOOLBAR_HEIGHT;
    frame.size.height = TOOLBAR_HEIGHT;
	[toolbar setFrame:frame];
    
	[contentView addSubview:toolbar];
}

- (void)loadView {
	categories = [Categories allCategories];
	
	CGRect frame = [[UIScreen mainScreen] applicationFrame];
	
	contentView = [[UIView alloc] initWithFrame:frame];
	self.view = contentView;
    
    action = NO_ACTION;
    
    [self createTableView];
    [self createDetailView];
    [self createToolbar];
    
    [self createToolbarItem];
    [self createNavigationItem];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
    [categoryNameField release];
    [categoryNameLabel release];
    [toolbar release];
    [detailView release];
	[tableView release];
	[contentView release];
    [super dealloc];
}


@end
