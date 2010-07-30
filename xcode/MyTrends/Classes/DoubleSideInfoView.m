
#import "DoubleSideInfoView.h"


@implementation DoubleSideInfoView

- (void)flipOver
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    [UIView beginAnimations:nil context:context];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:m_infoView cache:YES];
    [UIView setAnimationDuration:0.6];
    m_isFront = !m_isFront;
    if (m_isFront) {
        [m_infoView setText:m_text1 andButtonCaption:m_buttonCaption1];
    } else {
        [m_infoView setText:m_text2 andButtonCaption:m_buttonCaption2];
    }
    [UIView commitAnimations];
}

- (void)setText1:(NSString *)text1 buttonCaption1:(NSString *)buttonCaption1 
        andText2:(NSString *)text2 buttonCaption2:(NSString *)buttonCaption2
{
    m_text1 = text1;
    m_text2 = text2;
    m_buttonCaption1 = buttonCaption1;
    m_buttonCaption2 = buttonCaption2;
    if (m_isFront) {
        [m_infoView setText:m_text1 andButtonCaption:m_buttonCaption1];
    } else {
        [m_infoView setText:m_text2 andButtonCaption:m_buttonCaption2];
    }
}

- (id)initWithFrameAndText:(CGRect)frame superView:(UIView *)superView
                     text1:(NSString *)text1 buttonCaption1:(NSString *)buttonCaption1 
                     text2:(NSString *)text2 buttonCaption2:(NSString *)buttonCaption2
{
    if (self = [super init]) {
        m_infoView = [[InfoView alloc] initWithFrameAndText:frame target:self action:@selector(flipOver) text:text1 buttonCaption:buttonCaption1];
        [m_infoView setAlpha:0.8];
        m_superView = superView;
        [m_superView addSubview:m_infoView];
        m_isFront = YES;
        [self setText1:text1 buttonCaption1:buttonCaption1 andText2:text2 buttonCaption2:buttonCaption2];
    }
    return self;
}

- (void)show
{
    [m_infoView setAlpha:0.8];
}

- (void)hide
{
    [m_infoView setAlpha:0];
}

- (void)layout:(CGRect) frame
{
    [m_infoView resize:frame];
}

@end
