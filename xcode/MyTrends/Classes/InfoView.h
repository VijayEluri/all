
#import <UIKit/UIKit.h>

@interface InfoView : UIView {
    UITextView* m_textView;
    UIButton* m_button;
}

- (void)resize:(CGRect) frame;
- (id)initWithFrameAndText:(CGRect)frame target:(id)target action:(SEL)action
                     text:(NSString *)text buttonCaption:(NSString *)buttonCaption;
- (void)setText:(NSString *)text andButtonCaption:(NSString *)buttonCaption;

@end
