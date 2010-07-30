
#import <UIKit/UIKit.h>
#import "DoubleSideInfoView.h"
#import "MainToolbar.h"

@interface MainViewController : UIViewController<UIScrollViewDelegate> {
    UIScrollView* m_scrollView;
    UIButton* m_advertiseButton;
    UIButton* m_urlButton;
    DoubleSideInfoView* m_infoView;
    NSMutableArray* m_infoArray;
    MainToolbar* m_toolbar;
    int m_currentPage;
    BOOL m_bFullScreen;
    int m_statusBarHeight;
}

@end
