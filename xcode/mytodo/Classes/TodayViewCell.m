#import "TodayViewController.h"
#import "TaskViewController.h"
#import "TodayViewCell.h"
#import "MyButton.h"
#import "Consts.h"
#import "Task.h"

@implementation TodayViewCell

@synthesize task;
@synthesize todayViewController;

- (UIImage *)loadCheckedImage {
	return [UIImage imageNamed:@"checkedbox.png"];
}

- (UIImage *)loadUncheckedImage {
    return [UIImage imageNamed:@"box.png"];
}

- (void)updateTaskLabelColor {
	UIColor *color = nil;
	switch(task.priority) {
		case 0:
			color = [UIColor darkGrayColor];
			break;
		case 1:
			color = [UIColor blackColor];
			break;
		case 2:
			color = [UIColor blueColor];
			break;
		case 3:
			color = [UIColor redColor];
			break;
		default:
			color = [UIColor blackColor];
			break;
	}
	taskContentLabel.textColor = color;
}

- (void)updateTaskCountLabel {
	subTaskCountLabel.text = [NSString stringWithFormat:@"[%d/%d]", 
							  task.completedSubTasksCount, task.subTasksCount];
}

- (void)layoutLabels {
	CGRect titleFrame;
	CGRect contentRect = self.contentView.bounds;
	int contentWidth = contentRect.size.width;
	int titleFrameWidth = 0;
	
	if (task.subTasksCount > 0) {
		[self updateTaskCountLabel];
		
		CGSize size = [subTaskCountLabel.text sizeWithFont:[UIFont systemFontOfSize:SUBTASK_FONT_SIZE]];
		CGRect frame = CGRectMake(contentWidth - size.width - 4, contentRect.origin.y, 
								  size.width, contentRect.size.height);
		subTaskCountLabel.frame = frame;
		titleFrameWidth = contentWidth - size.width - 8 - CHECK_BUTTON_SIZE;
	} else {
		subTaskCountLabel.frame = CGRectZero;
		subTaskCountLabel.text = @"";
		titleFrameWidth = contentWidth - CHECK_BUTTON_SIZE;
	}
	
	
	titleFrame = CGRectMake(contentRect.origin.x + CHECK_BUTTON_SIZE, contentRect.origin.y, 
							titleFrameWidth - 8, contentRect.size.height);
	
	taskContentLabel.frame = titleFrame;
	
	[self updateTaskLabelColor];
}

- (void)layoutSubviews {
	[super layoutSubviews];
	
    CGRect contentRect = [self.contentView bounds];
	
	// layout the check button image
	CGRect frame = CGRectMake(contentRect.origin.x, 
							  contentRect.origin.y + (contentRect.size.height - CHECK_BUTTON_SIZE) / 2, 
							  CHECK_BUTTON_SIZE, CHECK_BUTTON_SIZE);
	completionCheckButton.frame = frame;
	
	UIImage *image = (task.completed) ? [self loadCheckedImage] : [self loadUncheckedImage];
	[completionCheckButton setBackgroundImage:image forState:UIControlStateNormal];
	
	taskContentLabel.text = task.content;
		
    [self layoutLabels];	
}

- (void)promptCannotCloseTask {
	UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You cannot close this task, as it has open sub tasks."
															 delegate:nil 
													cancelButtonTitle:@"Close" 
											   destructiveButtonTitle:nil 
													otherButtonTitles:nil];
	actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
	[actionSheet showInView:self.superview];
	[actionSheet release];
}

- (void)checkAction:(id)sender {
	if (task.completedSubTasksCount < task.subTasksCount) {
		[self promptCannotCloseTask];
	} else {
		task.completed = YES;
		[todayViewController removeCell:self];
	}
}

- (void)onAccessoryClick {
	TaskViewController *controller = [[TaskViewController alloc] init];
	[controller setParentTask:task];
    [controller loadView];
	[todayViewController.navigationController pushViewController:controller animated:YES];
	[controller release];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
				
		// cell's title label
		taskContentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		taskContentLabel.backgroundColor = [UIColor clearColor];
		taskContentLabel.opaque = NO;
		taskContentLabel.highlightedTextColor = [UIColor whiteColor];
		taskContentLabel.font = [UIFont systemFontOfSize:CONTENT_FONT_SIZE];
		
		//taskContentLabel.lineBreakMode = UILineBreakModeWordWrap;
		taskContentLabel.numberOfLines = 2;
		[self.contentView addSubview:taskContentLabel];
		
		// sub task count label
		subTaskCountLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		subTaskCountLabel.backgroundColor = [UIColor clearColor];
		subTaskCountLabel.opaque = NO;
		subTaskCountLabel.textColor = [UIColor blackColor];
		subTaskCountLabel.highlightedTextColor = [UIColor whiteColor];
		subTaskCountLabel.font = [UIFont systemFontOfSize:SUBTASK_FONT_SIZE];
		[self.contentView addSubview:subTaskCountLabel];
		
		// cell's check button
		completionCheckButton = [[MyButton buttonWithType:UIButtonTypeCustom] retain]; 
		completionCheckButton.frame = CGRectZero;
		completionCheckButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
		completionCheckButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
		[completionCheckButton addTarget:self action:@selector(checkAction:) forControlEvents:UIControlEventTouchDown];
		completionCheckButton.backgroundColor = [UIColor clearColor];
		[self.contentView addSubview:completionCheckButton];				
	}
	
	return self;
}

- (void)dealloc {
	NSLog(@"dealloc %@", self);
	[completionCheckButton release];
    [task release];
	[taskContentLabel release];
    [subTaskCountLabel release];
    [super dealloc];
}

@end
