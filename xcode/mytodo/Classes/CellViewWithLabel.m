
#import "CellViewWithLabel.h"
#import "Consts.h"

#define kCellHeight				25.0
#define kCellLeftOffset			8.0
#define kCellTopOffset			8.0
#define kPageControlWidth		160.0


@implementation CellViewWithLabel

@synthesize nameLabel;
@synthesize view;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)identifier
{
	if (self = [super initWithStyle:style reuseIdentifier:identifier])
	{
		// turn off selection use
		self.selectionStyle = UITableViewCellSelectionStyleNone;
		
		nameLabel = [[UILabel alloc] initWithFrame:self.frame];
		nameLabel.backgroundColor = [UIColor clearColor];
		nameLabel.opaque = NO;
		nameLabel.textColor = [UIColor blackColor];
		nameLabel.highlightedTextColor = [UIColor blackColor];
		nameLabel.font = [UIFont boldSystemFontOfSize:CONTENT_FONT_SIZE];
		[self.contentView addSubview:nameLabel];
	}
	return self;
}

- (void)setView:(UIView *)inView
{
	if (view)
		[view removeFromSuperview];
	view = inView;
	[self.view retain];
	[self.contentView addSubview:inView];
	
	[self layoutSubviews];
}

- (void)layoutSubviews
{	
	[super layoutSubviews];
    CGRect contentRect = [self.contentView bounds];
	
	CGRect frame = CGRectMake(contentRect.origin.x + kCellLeftOffset, kCellTopOffset, contentRect.size.width, kCellHeight);
	nameLabel.frame = frame;
	
	if ([view isKindOfClass:[UIPageControl class]])
	{
		CGRect frame = self.view.frame;
		frame.size.width = kPageControlWidth;
		self.view.frame = frame;
	}

	CGRect uiFrame = CGRectMake(contentRect.size.width - self.view.bounds.size.width - kCellLeftOffset,
								round((contentRect.size.height - self.view.bounds.size.height) / 2.0),
								self.view.bounds.size.width,
								self.view.bounds.size.height);
	view.frame = uiFrame;
}

- (void)dealloc
{
	NSLog(@"dealloc %@", self);
	[nameLabel release];
	[view release];
	
    [super dealloc];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
	[super setSelected:selected animated:animated];

	nameLabel.highlighted = selected;
}

@end
