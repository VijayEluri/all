
#import <UIKit/UIKit.h>


@interface ContentView : UIView {
    UIImageView* m_imageView;
}
- (id)initWithFrame:(CGRect)frame imageView:(UIImageView *)imageView;
- (void)layoutImageView:(CGRect)frame;

@end
