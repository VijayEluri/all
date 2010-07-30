
#import "MainToolbar.h"

@implementation MainToolbar

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.barStyle = UIBarStyleBlack;
        self.translucent = YES;
        
        UIBarButtonItem* aboutItem = [[UIBarButtonItem alloc] initWithTitle:@"关于" 
                                                                      style:UIBarButtonItemStyleBordered
                                                                     target:nil 
                                                                     action:nil];
        
        UIBarButtonItem *actionItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction
                                                                                    target:nil 
                                                                                    action:nil];
        UIBarButtonItem *imageItem = [[UIBarButtonItem alloc] initWithImage: [UIImage imageNamed:@"trends.png"] style:UIBarButtonItemStylePlain 
                                                                                          target:nil action:nil];
        UIBarButtonItem* titleItem = [[UIBarButtonItem alloc] initWithTitle:@"时尚表情" 
                                                                      style:UIBarButtonItemStylePlain
                                                                     target:nil 
                                                                     action:nil];
        
        UIBarButtonItem *searchItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:nil action:nil];
        
        UIBarButtonItem *flexItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace 
                                                                                  target:nil
                                                                                  action:nil];
        
        [self setItems: [NSArray arrayWithObjects:aboutItem, flexItem, imageItem, titleItem, flexItem, searchItem, actionItem, nil]];
        [aboutItem release];
        [actionItem release];
        [searchItem release];
        [flexItem release];
        
    }
    return self;
}

- (void)about:(id)sender
{
    NSLog(@"about!");
}

@end
