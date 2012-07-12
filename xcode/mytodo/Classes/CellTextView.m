
#import "CellTextView.h"

#define kInsertValue	0.0

@implementation CellTextView

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)identifier {
	self = [super initWithStyle:style reuseIdentifier:identifier];
	if (self) {
		self.selectionStyle = UITableViewCellSelectionStyleNone;
	}
	return self;
}

- (void)setView:(UITextView *)inView {
	textView = inView;
	[self.contentView addSubview:textView];
	[self layoutSubviews];
}

- (void)layoutSubviews {
	[super layoutSubviews];
	
	CGRect contentRect = [self.contentView bounds];
	textView.frame  = CGRectMake(contentRect.origin.x + kInsertValue,
								 contentRect.origin.y + 6.0,
								 contentRect.size.width - (kInsertValue * 2),
								 contentRect.size.height - 6.0);
}

@end
