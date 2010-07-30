
#import <UIKit/UIKit.h>
#import "InfoView.h"

@interface DoubleSideInfoView : NSObject {
    InfoView* m_infoView;
    UIView* m_superView;
    NSString* m_text1;
    NSString* m_text2;
    NSString* m_buttonCaption1;
    NSString* m_buttonCaption2;
    BOOL m_isFront;
}

- (id)initWithFrameAndText:(CGRect)frame superView:(UIView *)superView
                     text1:(NSString *)text1 buttonCaption1:(NSString *)buttonCaption1 
                     text2:(NSString *)text2 buttonCaption2:(NSString *)buttonCaption2;
- (void)show;
- (void)hide;
- (void)layout:(CGRect) frame;
- (void)setText1:(NSString *)text1 buttonCaption1:(NSString *)buttonCaption1 
        andText2:(NSString *)text2 buttonCaption2:(NSString *)buttonCaption2;

@end
