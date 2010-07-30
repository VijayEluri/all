
#import "MyWebView.h"

@implementation MyWebView

-(void) Close
{
    [self removeFromSuperview];
    [self release];
}

- (void) createToolbar:(CGRect)frame  
{
    UIToolbar* toolBar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, 44)];
        
    UIBarButtonItem* hideItem = [[UIBarButtonItem alloc] initWithTitle:@"关闭" 
                                                                 style:UIBarButtonItemStyleBordered
                                                                target:self 
                                                                action:@selector(Close)];
    UIBarButtonItem *flexItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace 
                                                                              target:nil
                                                                              action:nil];
        
    [toolBar setItems: [NSArray arrayWithObjects:flexItem, hideItem, nil]];
    [hideItem release];
    [flexItem release];

    [self addSubview:toolBar];
        
    [toolBar release];

}

- (void) createWebview:(CGRect)frame  
{
    UIWebView* webView = [[UIWebView alloc] initWithFrame:CGRectMake(0, 44, frame.size.width, frame.size.height - 44)];
    [webView loadHTMLString:NSLocalizedString(@"web", @"") baseURL:nil];
    [self addSubview:webView];
    [webView release];
}

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
//        [self setBackgroundColor:[UIColor whiteColor]];
        [self createToolbar:frame];
        [self createWebview:frame];
    }
    return self;
}

@end
