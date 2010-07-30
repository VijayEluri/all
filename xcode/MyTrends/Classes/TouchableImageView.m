
#import "TouchableImageView.h"


@implementation TouchableImageView

- (id)initWithImage:(UIImage *)image target:(id)target action:(SEL)action
{
    m_target = target;
    m_action = action;
    if (self = [super initWithImage:image]) {
        self.userInteractionEnabled = YES;
    }
    return self;
}

- (BOOL)canBecomeFirstResponder
{
    return YES;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [m_target performSelector:m_action];
}

@end
