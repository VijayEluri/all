
#import "TaskViewCell.h"
#import "TaskViewController.h"
#import "Consts.h"
#import "MyButton.h"
#import "Task.h"

@implementation TaskViewCell

@synthesize task = theTask;
@synthesize taskViewController;
@synthesize nowDate;

- (UIImage *)loadCheckedImage {
	return [UIImage imageNamed:@"checkedbox.png"];
}

- (UIImage *)loadUncheckedImage {
    return [UIImage imageNamed:@"box.png"];
}

- (void)onAccessoryClick {
	TaskViewController *controller = [[TaskViewController alloc] init];
	[controller setParentTask:theTask];
    [controller loadView];
	[taskViewController.navigationController pushViewController:controller animated:YES];
}

- (void)updateTaskLabelColor {
	if (theTask.completed) {
		taskContentLabel.textColor = [UIColor lightGrayColor];
		subTaskCountLabel.textColor = [UIColor lightGrayColor];
	} else {
		UIColor *color = nil;
		switch(theTask.priority) {
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
		subTaskCountLabel.textColor = color;
	}
}

- (void)layoutCompletionDateLabel {
	CGRect contentRect = self.contentView.bounds;
	if (theTask.completed) {
		dateLabel.text = theTask.completionDate;
		CGSize size = [theTask.completionDate sizeWithFont:[UIFont systemFontOfSize:DATE_FONT_SIZE]];
		CGRect frame = CGRectMake(contentRect.origin.x + CHECK_BUTTON_SIZE, 
								  contentRect.size.height - contentRect.origin.y - DATE_FONT_SIZE - 3,
								  size.width, size.height);
		dateLabel.frame = frame;
		dateLabel.textColor = [UIColor lightGrayColor];
	} else {
		if ([theTask.dueDate length] > 0) {
			dateLabel.text = theTask.dueDate;
			CGSize size = [dateLabel.text sizeWithFont:[UIFont systemFontOfSize:DATE_FONT_SIZE]];
			CGRect frame = CGRectMake(contentRect.origin.x + CHECK_BUTTON_SIZE, 
									  contentRect.size.height - contentRect.origin.y - DATE_FONT_SIZE - 3,
									  size.width, size.height);
			dateLabel.frame = frame;
			switch([theTask.dueDate compare:nowDate]) {
				case NSOrderedDescending: //due after today
					dateLabel.textColor = [UIColor darkGrayColor];
					break;
				case NSOrderedAscending: // over due
					dateLabel.textColor = [UIColor redColor];
					break;
				default: // today
					dateLabel.textColor = [UIColor blueColor];
			}
		} else {
			dateLabel.frame = CGRectZero;
		}
	}
}

- (void)updateTaskCountLabel {
	subTaskCountLabel.text = [NSString stringWithFormat:@"[%d/%d]", 
							  theTask.completedSubTasksCount, theTask.subTasksCount];
}

- (void)layoutLabels {
	CGRect titleFrame;
	CGRect contentRect = self.contentView.bounds;
	int contentWidth = contentRect.size.width;
	int titleFrameWidth = 0;
	
	if (theTask.subTasksCount > 0) {
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
	

	int completionOffset = theTask.completed ? 15 : ([theTask.dueDate length] > 0 ? 15 : 0);
	titleFrame = CGRectMake(contentRect.origin.x + CHECK_BUTTON_SIZE, contentRect.origin.y, 
							titleFrameWidth, contentRect.size.height - completionOffset);
	
	taskContentLabel.frame = titleFrame;
	
	[self layoutCompletionDateLabel];
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
	
	UIImage *image = (theTask.completed) ? [self loadCheckedImage] : [self loadUncheckedImage];
	[completionCheckButton setBackgroundImage:image forState:UIControlStateNormal];

	taskContentLabel.text = theTask.content;
	
	
    [self layoutLabels];	
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (actionSheet.numberOfButtons == 1) return;

	if (buttonIndex == 0) {
		theTask.completed = !theTask.completed;
		UIImage *image = (theTask.completed) ? [self loadCheckedImage] : [self loadUncheckedImage];
		[completionCheckButton setBackgroundImage:image forState:UIControlStateNormal];
		
		[taskViewController moveCellToNewSection:self];
	}	
}

- (void)askToReopenParentTasks {
	UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"Do you want to reopen this task and it's related parent tasks ?"
															 delegate:self 
													cancelButtonTitle:@"No" 
											   destructiveButtonTitle:@"Yes" 
													otherButtonTitles:nil];
	actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
	[actionSheet showInView:taskViewController.view];
}

- (void)promptCannotCloseTask {
	UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You cannot close this task, as it has open sub tasks."
															 delegate:self 
													cancelButtonTitle:@"Close" 
											   destructiveButtonTitle:nil 
													otherButtonTitles:nil];
	actionSheet.actionSheetStyle = UIActionSheetStyleDefault;
	[actionSheet showInView:taskViewController.view];
}

- (void)checkAction:(id)sender {
	if (theTask.completed) {
		[self askToReopenParentTasks];
	} else {
		if (theTask.completedSubTasksCount < theTask.subTasksCount) {
			[self promptCannotCloseTask];
		} else {
			theTask.completed = !theTask.completed;
			UIImage *image = (theTask.completed) ? [self loadCheckedImage] : [self loadUncheckedImage];
			[completionCheckButton setBackgroundImage:image forState:UIControlStateNormal];			
			[taskViewController moveCellToNewSection:self];
		}
	}
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
		
		self.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
		
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
		completionCheckButton = [MyButton buttonWithType:UIButtonTypeCustom]; 
		completionCheckButton.frame = CGRectZero;
		completionCheckButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
		completionCheckButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
		[completionCheckButton addTarget:self action:@selector(checkAction:) forControlEvents:UIControlEventTouchDown];
		completionCheckButton.backgroundColor = [UIColor clearColor];
		[self.contentView addSubview:completionCheckButton];		
		
		dateLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		dateLabel.backgroundColor = [UIColor clearColor];
		dateLabel.opaque = NO;
		dateLabel.textColor = [UIColor lightGrayColor];
		dateLabel.highlightedTextColor = [UIColor whiteColor];
		dateLabel.font = [UIFont systemFontOfSize:DATE_FONT_SIZE];
		[self.contentView addSubview:dateLabel];
	}
	
	return self;	
}

@end
