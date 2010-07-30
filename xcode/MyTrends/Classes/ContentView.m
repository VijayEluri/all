
#import "ContentView.h"


@implementation ContentView

- (id)initWithFrame:(CGRect)frame imageView:(UIImageView *)imageView
{
    if (self = [super initWithFrame:frame]) {
        m_imageView = [imageView retain];
        [self addSubview:m_imageView];
    }
    return self;
}

- (void)layoutImageView:(CGRect)frame
{
    float imageWidth = m_imageView.image.size.width;
    float imageHeight = m_imageView.image.size.height;
    float frameWidth = frame.size.width;
    float frameHeight = frame.size.height;
    float widthRatio = frameWidth / imageWidth;
    float heightRatio = frameHeight / imageHeight;
    float ratio = widthRatio;
    if (heightRatio < widthRatio) ratio = heightRatio;
    imageWidth = imageWidth * ratio;
    imageHeight = imageHeight * ratio;
    
    m_imageView.frame = CGRectMake((frameWidth - imageWidth) / 2, (frameHeight - imageHeight) / 2, imageWidth, imageHeight);
//    NSLog(@"x: %f, y: %f, width: %f, height: %f\n", m_imageView.frame.origin.x, m_imageView.frame.origin.y, imageWidth, imageHeight);
}

- (void)dealloc
{
    [m_imageView release];
    [super dealloc];
}

@end
