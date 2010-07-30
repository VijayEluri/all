
#import "SettingViewController.h"
#import "SettingViewDelegate.h"
#import "SettingViewDataSource.h"

@implementation SettingViewController

@synthesize animationSwitch;

- (void)createAnimationSwitch {
	if (animationSwitch == nil) {
		animationSwitch = [[UISwitch alloc] initWithFrame:CGRectZero];
	}
}

- (void)loadView {
	
	tableViewDelegate = [[SettingViewDelegate alloc] init];
	tableViewDataSource = [[SettingViewDataSource alloc] init];

	tableViewDataSource.settingViewController = self;
	
	tableView = [[UITableView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] 
											 style:UITableViewStyleGrouped];	
	tableView.delegate = tableViewDelegate;
	tableView.dataSource = tableViewDataSource;
	tableView.scrollEnabled = YES;
	tableView.autoresizesSubviews = YES;
	
	[self createAnimationSwitch];
	
	self.view = tableView;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);

	[animationSwitch release];
	
	tableView.delegate = nil;
	tableView.dataSource = nil;
	[tableView release];
	[tableViewDelegate release];
	[tableViewDataSource release];
	
	[super dealloc];
}

@end

