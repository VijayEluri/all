
#import "InfoView.h"

@implementation InfoView

- (id)initWithFrameAndText:(CGRect)frame target:(id)target action:(SEL)action
                      text:(NSString *)text buttonCaption:(NSString *)buttonCaption
{
    if (self = [super initWithFrame:frame]) {
        [self setBackgroundColor: [UIColor blackColor]];
        
        m_textView = [[UITextView alloc] initWithFrame:frame];
        [m_textView setBackgroundColor: [UIColor clearColor]];
        [m_textView setTextColor: [UIColor whiteColor]];
        [m_textView setFont: [UIFont systemFontOfSize:18]];
        [m_textView setText:text];
        [m_textView setEditable:NO];
        
        m_button = [UIButton buttonWithType:UIButtonTypeCustom];
        [m_button setTitle:buttonCaption forState:UIControlStateNormal];
        [m_button addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:m_textView];
        [self addSubview:m_button];
    }
    return self;
}

- (void)setText:(NSString *)text andButtonCaption:(NSString *)buttonCaption
{
    [m_textView setText:text];
    [m_button setTitle:buttonCaption forState:UIControlStateNormal];
}

- (void)dealloc
{
    [m_textView release];
    [m_button release];
    [super dealloc];
}

- (void)resize:(CGRect) frame
{
    const int buttonWidth = 100;
    const int buttonHeight = 30;
    self.frame = frame;
    m_textView.frame = CGRectMake(20, 20, frame.size.width - 20, frame.size.height - buttonHeight - 20 - 40 );
    m_button.frame = CGRectMake(frame.size.width - buttonWidth - 20, frame.size.height - buttonHeight - 20, buttonWidth, buttonHeight);
}

- (void)drawRoundedRect:(CGRect)rect inContext:(CGContextRef)context
{
    float radius = 5.0f;
    
    CGContextBeginPath(context);
    //	CGContextSetGrayFillColor(context, 0.9, 0.9);
    CGContextSetStrokeColorWithColor(context, [UIColor darkGrayColor].CGColor);
	CGContextMoveToPoint(context, CGRectGetMinX(rect) + radius, CGRectGetMinY(rect));
    CGContextAddArc(context, CGRectGetMaxX(rect) - radius, CGRectGetMinY(rect) + radius, radius, 3 * M_PI / 2, 0, 0);
    CGContextAddArc(context, CGRectGetMaxX(rect) - radius, CGRectGetMaxY(rect) - radius, radius, 0, M_PI / 2, 0);
    CGContextAddArc(context, CGRectGetMinX(rect) + radius, CGRectGetMaxY(rect) - radius, radius, M_PI / 2, M_PI, 0);
    CGContextAddArc(context, CGRectGetMinX(rect) + radius, CGRectGetMinY(rect) + radius, radius, M_PI, 3 * M_PI / 2, 0);
	
    CGContextClosePath(context);
    CGContextDrawPath(context, kCGPathStroke);
}

- (void)drawRect:(CGRect)rect
{
    [super drawRect:rect];
    CGContextRef ctxt = UIGraphicsGetCurrentContext();	
    [self drawRoundedRect:m_button.frame inContext:ctxt];
}

@end
