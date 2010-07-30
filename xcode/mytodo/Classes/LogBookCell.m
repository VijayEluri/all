
#import "LogBookCell.h"
#import "Task.h"
#import "Consts.h"

@implementation LogBookCell

@synthesize task = theTask;

- (UIImage *)loadCheckedImage {
	return [UIImage imageNamed:@"checkedbox.png"];
}

- (void)layoutSubviews {
	[super layoutSubviews];
	
    CGRect contentRect = [self.contentView bounds];
	
	// layout the check button image
	CGRect frame = CGRectMake(contentRect.origin.x, 
							  contentRect.origin.y + (contentRect.size.height - CHECK_BUTTON_SIZE) / 2, 
							  CHECK_BUTTON_SIZE, CHECK_BUTTON_SIZE);
	tickImageView.frame = frame;
		
	taskContentLabel.text = theTask.content;
	if (theTask.completionDate.length > 11) {
		completionDateLabel.text = [theTask.completionDate substringFromIndex:11];
	} else {
		completionDateLabel.text = @"";
	}

	CGRect titleFrame;
	int contentWidth = contentRect.size.width;
	int titleFrameWidth = 0;

	titleFrameWidth = contentWidth - CHECK_BUTTON_SIZE;
	int completionOffset = 15;
	
	titleFrame = CGRectMake(contentRect.origin.x + CHECK_BUTTON_SIZE, contentRect.origin.y, 
							titleFrameWidth, contentRect.size.height - completionOffset);
	
	taskContentLabel.frame = titleFrame;
	CGSize size = [completionDateLabel.text sizeWithFont:[UIFont systemFontOfSize:DATE_FONT_SIZE]];
	frame = CGRectMake(320 - size.width - 4, 
					   contentRect.size.height - contentRect.origin.y - DATE_FONT_SIZE - 3,
					   size.width, size.height);
	completionDateLabel.frame = frame;	
	
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier])	{
		// cell's title label
		taskContentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		taskContentLabel.backgroundColor = [UIColor clearColor];
		taskContentLabel.opaque = NO;
		taskContentLabel.highlightedTextColor = [UIColor whiteColor];
		taskContentLabel.font = [UIFont systemFontOfSize:CONTENT_FONT_SIZE];
		
		//taskContentLabel.lineBreakMode = UILineBreakModeWordWrap;
		taskContentLabel.numberOfLines = 2;
		[self.contentView addSubview:taskContentLabel];
						
		completionDateLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		completionDateLabel.backgroundColor = [UIColor clearColor];
		completionDateLabel.opaque = NO;
		completionDateLabel.textColor = [UIColor grayColor];
		completionDateLabel.highlightedTextColor = [UIColor whiteColor];
		completionDateLabel.font = [UIFont systemFontOfSize:DATE_FONT_SIZE];
		[self.contentView addSubview:completionDateLabel];

		tickImageView = [[UIImageView alloc] initWithImage:[self loadCheckedImage]];
		tickImageView.backgroundColor = [UIColor clearColor];
		tickImageView.opaque = NO;
		[self.contentView addSubview:tickImageView];
	}
	
	return self;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[taskContentLabel release];
	[completionDateLabel release];
	[tickImageView release];
	[theTask release];
    [super dealloc];
}

@end
