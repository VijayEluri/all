
#import "MyTrendsAppDelegate.h"

@implementation MyTrendsAppDelegate

@synthesize window;


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {    
	
    // Override point for customization after application launch
    m_mainViewController = [[MainViewController alloc] init];
    [m_mainViewController loadView];

    [window addSubview:m_mainViewController.view];
    [window makeKeyAndVisible];
//	[[UIApplication sharedApplication] setStatusBarStyle: UIStatusBarStyleBlackTranslucent];
    
    return YES;
}


- (void)dealloc {
    [window release];
    [m_mainViewController release];
    [super dealloc];
}


@end
