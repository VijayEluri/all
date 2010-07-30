
#import "StartMenuView.h"
#import "TaskViewToolbarItemsDelegate.h"
#import "LogBookViewController.h"
#import "CategoryViewController.h"
#import "TodayViewController.h"
#import "SettingViewController.h"

@implementation StartMenuView

@synthesize toolbarItemsDelegate;
@synthesize navigationController;

- (void)showLogBook {
	[toolbarItemsDelegate hideStartMenu];
	LogBookViewController *controller = [[LogBookViewController alloc] init];
	[controller loadView];
	controller.title = @"Log Book";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)showCategories {
	[toolbarItemsDelegate hideStartMenu];
	CategoryViewController *controller = [[CategoryViewController alloc] init];
	[controller loadView];
	controller.title = @"Categories";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)showToday {
	[toolbarItemsDelegate hideStartMenu];
	TodayViewController *controller = [[TodayViewController alloc] init];
	[controller loadView];
    controller.title = @"Today";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (void)showSettings {
	[toolbarItemsDelegate hideStartMenu];
	SettingViewController *controller = [[SettingViewController alloc] init];
	[controller loadView];
	controller.title = @"Settings";
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];	
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return @"Views Menu";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return 4;
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)aTableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellAccessoryNone;
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
			cell.textLabel.text = @"Today";
			break;
		case 2:
			cell.textLabel.text = @"Categories";
			break;
		case 3:
			cell.textLabel.text = @"Settings";
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
			[self showToday];
			break;
		case 2:
			[self showCategories];
			break;
		case 3:
			[self showSettings];
			break;
	}
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
		frame.origin.x = 2;
		frame.origin.y = 2;
		frame.size.width -= 4;
		frame.size.height -= 4;
		tableView = [[UITableView alloc] initWithFrame:frame
												 style:UITableViewStyleGrouped];	
		tableView.delegate = self;
		tableView.dataSource = self;
		tableView.scrollEnabled = YES;
		tableView.autoresizesSubviews = YES;
		tableView.backgroundColor = [UIColor clearColor];
		
		[self addSubview:tableView];
		
		self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)drawRoundedRect:(CGRect)rect inContext:(CGContextRef)context
{
    float radius = 5.0f;
    
    CGContextBeginPath(context);
	CGContextSetGrayFillColor(context, 0.9, 0.9);
	CGContextMoveToPoint(context, CGRectGetMinX(rect) + radius, CGRectGetMinY(rect));
    CGContextAddArc(context, CGRectGetMaxX(rect) - radius, CGRectGetMinY(rect) + radius, radius, 3 * M_PI / 2, 0, 0);
    CGContextAddArc(context, CGRectGetMaxX(rect) - radius, CGRectGetMaxY(rect) - radius, radius, 0, M_PI / 2, 0);
    CGContextAddArc(context, CGRectGetMinX(rect) + radius, CGRectGetMaxY(rect) - radius, radius, M_PI / 2, M_PI, 0);
    CGContextAddArc(context, CGRectGetMinX(rect) + radius, CGRectGetMinY(rect) + radius, radius, M_PI, 3 * M_PI / 2, 0);
	
    CGContextClosePath(context);
    CGContextDrawPath(context, kCGPathFillStroke);
}

- (void)drawRect:(CGRect)rect {
	CGRect boxRect = self.bounds;
    CGContextRef ctxt = UIGraphicsGetCurrentContext();	
	boxRect = CGRectInset(boxRect, 1.0f, 1.0f);
    [self drawRoundedRect:boxRect inContext:ctxt];
}

- (void)dealloc {
	[tableView release];
    [super dealloc];
}


@end
