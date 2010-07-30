
#import "MainViewController.h"
#import "TouchableImageView.h"
#import "ContentView.h"
#import "Info.h"
#import "MyWebView.h"

@implementation MainViewController

#pragma mark Advertise Button

- (void)layoutAdvertiseButton:(UIInterfaceOrientation)orientation
{
    const int xOffset = -20;
    const int yOffset = -20;
    const int width = 80;
    const int height = 25;
    
    CGRect bounds = UIScreen.mainScreen.bounds;
    
    int x = bounds.size.width - width + xOffset;
    int y = bounds.size.height - height + yOffset;
    if ((orientation == UIInterfaceOrientationLandscapeLeft) 
        || (orientation == UIInterfaceOrientationLandscapeRight)) {
        x = bounds.size.height - width + xOffset;
        y = bounds.size.width - height + yOffset;
    }
    
    if (![UIApplication sharedApplication].statusBarHidden) {
        y -= m_statusBarHeight;
    }
    
    m_advertiseButton.frame = CGRectMake(x, y, width, height);
    m_urlButton.frame = CGRectMake(x - 20 - width, y, width, height);
}

- (void)advertiseAction
{
    MyWebView* webView = [[MyWebView alloc] initWithFrame:CGRectMake(30, 50, 600, 600)];
    [self.view addSubview:webView];
//    [self.view bringSubviewToFront:webView];
}

- (void)urlAction
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://www.minichina.com.cn"]];
}

- (void)showAdvertiseButton
{
    [m_advertiseButton setAlpha:1];
    [m_urlButton setAlpha:1];
    [self.view bringSubviewToFront:m_advertiseButton];
    [self.view bringSubviewToFront:m_urlButton];
}

- (void)hideAdvertiseButton
{
    [m_advertiseButton setAlpha:0];
    [m_urlButton setAlpha:0];
}

- (void)createAdvertiseButton
{
//    m_advertiseButton = [[UIButton buttonWithType:UIButtonTypeRoundedRect] retain];
    m_advertiseButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
    [m_advertiseButton setTitle:@"我选" forState:UIControlStateNormal];
    [m_advertiseButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [m_advertiseButton setBackgroundColor:[UIColor clearColor]];
    [m_advertiseButton addTarget:self action:@selector(advertiseAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:m_advertiseButton];    

    m_urlButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
    [m_urlButton setTitle:@"Mini中国" forState:UIControlStateNormal];
    [m_urlButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [m_urlButton setBackgroundColor:[UIColor clearColor]];
    [m_urlButton addTarget:self action:@selector(urlAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:m_urlButton];    
}

#pragma mark Layout Views

- (void)layoutInfoViews:(UIInterfaceOrientation)orientation
{
    const int xOffset = 20;
    const int yOffset = -20;
    const int width = 400;
    const int height = 200;
    
    CGRect bounds = UIScreen.mainScreen.bounds;
    
    int y = bounds.size.height + yOffset;
    if ((orientation == UIInterfaceOrientationLandscapeLeft) 
        || (orientation == UIInterfaceOrientationLandscapeRight)) {
        y = bounds.size.width + yOffset;
    }
    
    if (![UIApplication sharedApplication].statusBarHidden) {
        y -= m_statusBarHeight;
    }
    
    [m_infoView layout:CGRectMake(xOffset, y - height, width, height)];
}

- (void)layoutToolbar:(UIInterfaceOrientation)orientation
{
    const int toolbarHeight = 44;
    const int toolbarHeightLandscape = 32;
    
    CGRect bounds = UIScreen.mainScreen.bounds;
    
    int width = bounds.size.width;
    int height = toolbarHeight;
    if ((orientation == UIInterfaceOrientationLandscapeLeft) 
        || (orientation == UIInterfaceOrientationLandscapeRight)) {
        width = bounds.size.height;
        height = toolbarHeightLandscape;
    }
    
    int yOffset = 0;
    if ([UIApplication sharedApplication].statusBarHidden) {
        yOffset = m_statusBarHeight;
    }
    
    m_toolbar.frame = CGRectMake(0, yOffset, width, height);
}

- (void)layoutScrollViews:(UIInterfaceOrientation)orientation 
{    
    CGRect bounds = UIScreen.mainScreen.bounds;
    
    CGRect frame = CGRectMake(0, -m_statusBarHeight, 0, 0);
    if ([UIApplication sharedApplication].statusBarHidden) {
        frame.origin.y = 0;
    }

    if ((orientation == UIInterfaceOrientationLandscapeLeft) 
        || (orientation == UIInterfaceOrientationLandscapeRight)) {
        frame.size = CGSizeMake(bounds.size.height, bounds.size.width);
    } else {
        frame.size = bounds.size;
    }
    
    self.view.frame = frame;
    m_scrollView.frame = frame;
    
    int i = 0;
    for (UIView* view in m_scrollView.subviews) {
        if ([view isKindOfClass:ContentView.class]) {
            ContentView* contentView = (ContentView *)view;
            [contentView layoutImageView:frame];
            contentView.frame = CGRectMake(frame.size.width * i, 0, frame.size.width, frame.size.height);
            i ++;
        }
    }
        
    m_scrollView.contentSize = CGSizeMake(frame.size.width * i, frame.size.height);
    int page = m_currentPage;
    m_scrollView.contentOffset = CGPointMake(frame.size.width * m_currentPage, 0);
    m_currentPage = page;
}

- (void)layoutAllViews:(UIInterfaceOrientation)orientation
{
    [self layoutScrollViews:orientation];
    [self layoutInfoViews:orientation];
    [self layoutToolbar:orientation];
    [self layoutAdvertiseButton:orientation];
}

#pragma mark Tap Gesture

- (void)hideAll
{
    [m_infoView hide];
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationFade];
    m_toolbar.alpha = 0;
}

- (void)showAll
{
    [m_infoView show];
    [[UIApplication sharedApplication] setStatusBarHidden:NO withAnimation:UIStatusBarAnimationFade];
    m_toolbar.alpha = 0.8;
}

- (void)handleSingleTapGesture
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    [UIView beginAnimations:nil context:context];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.2];

    m_bFullScreen = !m_bFullScreen;
    if (m_bFullScreen) {
        [self hideAll];
    } else {
        [self showAll];
    }
    
    [UIView commitAnimations];
}

#pragma mark Info Strings

- (void)loadInfoStrings
{
    m_infoArray = [[NSMutableArray alloc] initWithCapacity:7];
    for (int i = 0; i < 7; ++i) {
        NSString* catalogName = [NSString stringWithFormat:@"catalog%d", i + 1];
        NSString* textName = [NSString stringWithFormat:@"text%d", i + 1];
        NSString* tipName = [NSString stringWithFormat:@"tip%d", i + 1];
        Info* info = [[Info alloc] init];
        info.catalog = NSLocalizedString(catalogName, @"");
        info.text = NSLocalizedString(textName, @"");
        info.tip = NSLocalizedString(tipName, @"");
        [m_infoArray addObject:info];
        [info release];
    }
    Info* info = [[Info alloc] init];
    info.catalog = @"广告";
    info.text = @"我选我MINI";
    info.tip = @"选择MINI，有机会中大奖";
    [m_infoArray addObject:info];
}

- (void)updateInfoStrings
{
    Info* info = [m_infoArray objectAtIndex:m_currentPage];
    [m_infoView setText1:info.text buttonCaption1:@"Tip" andText2:info.tip buttonCaption2:@"Caption"];
}

#pragma mark Init Views

- (void)createScrollView
{
    m_scrollView = [[UIScrollView alloc] initWithFrame:self.view.frame];
    m_scrollView.clipsToBounds = YES;
    m_scrollView.scrollEnabled = YES;
    m_scrollView.pagingEnabled = YES;
    m_scrollView.showsHorizontalScrollIndicator = NO;
    m_scrollView.showsVerticalScrollIndicator = NO;
    m_scrollView.bounces = NO;
    m_scrollView.delegate = self;
    [self.view addSubview:m_scrollView];
}

- (void)createView
{
    CGRect frame = [UIScreen mainScreen].applicationFrame;
    UIView* view = [[UIView alloc] initWithFrame:frame];
    [view setBackgroundColor:[UIColor blackColor]];
    self.view = view;
    [view release];
}

- (void)createContentViews
{
    for (int i = 0; i < 8; ++i) {
        NSString* fileName = [NSString stringWithFormat:@"%d.png", i + 1];
        UIImage* image = [UIImage imageNamed:fileName];
        UIImageView* imageView = [[TouchableImageView alloc] initWithImage:image 
                                                                    target:self 
                                                                    action:@selector(handleSingleTapGesture)];
        ContentView* contentView = [[ContentView alloc] initWithFrame:CGRectZero imageView:imageView];
        [m_scrollView addSubview:contentView];
        [contentView release];
        [imageView release];
    }
}

- (void)createInfoViews
{
    CGRect frame = CGRectMake(100, 100, 200, 100);
    m_infoView = [[DoubleSideInfoView alloc] initWithFrameAndText:frame 
                                                       superView:self.view
                                                           text1:NSLocalizedString(@"text1", @"")
                                                   buttonCaption1:@"小贴士"
                                                           text2:@"谁也买不上" 
                                                  buttonCaption2:@"小贴士"];
}

- (void)createToolbar
{
    CGRect frame = CGRectMake(0, 0, 100, 100);
    m_toolbar = [[MainToolbar alloc] initWithFrame:frame];
    [self.view addSubview:m_toolbar];
}

- (void)loadView 
{    
    m_currentPage = 0;
    m_bFullScreen = NO;
    m_statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;

    [self createView];
    [self createAdvertiseButton];
    [self hideAdvertiseButton];

    [self createScrollView];
    [self createContentViews];
    [self createInfoViews];
    [self createToolbar];
    [self loadInfoStrings];
    [self updateInfoStrings];
    
    
    m_scrollView.contentOffset = CGPointZero;
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [self layoutAllViews:UIInterfaceOrientationPortrait];
    CGRect bounds = UIScreen.mainScreen.bounds;
    m_toolbar.frame = CGRectMake(0, m_statusBarHeight * 2, bounds.size.width, 32);
}

#pragma mark UIViewController Events

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    [self layoutAllViews:toInterfaceOrientation];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation 
{
    return YES;
}

#pragma mark UIScrollViewDelegate

- (void)updateCurrentPageNumber
{
    CGRect bounds = UIScreen.mainScreen.bounds;
    CGPoint offset = m_scrollView.contentOffset;
    int x = (int) offset.x;
    
    int pageWidth = (int) bounds.size.width;
    if ((self.interfaceOrientation == UIInterfaceOrientationLandscapeLeft) 
        || (self.interfaceOrientation == UIInterfaceOrientationLandscapeRight)) {
        pageWidth = (int) bounds.size.height;
    }
    
    if (x % pageWidth == 0) {
        m_currentPage = x / pageWidth;
        [self updateInfoStrings];
        if (m_currentPage == 7) {
            [self showAdvertiseButton];
        } else {
            [self hideAdvertiseButton];
        }
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self updateCurrentPageNumber];
}

#pragma mark Memory Related

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
    [super viewDidUnload];
}


- (void)dealloc {
    [super dealloc];
    [m_scrollView release];
    [m_toolbar release];
}

@end
