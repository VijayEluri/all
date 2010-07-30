
#import <UIKit/UIKit.h>


@interface TouchableImageView : UIImageView {
    id m_target;
    SEL m_action;
}

- (id)initWithImage:(UIImage *)image target:(id)target action:(SEL)action;

@end
