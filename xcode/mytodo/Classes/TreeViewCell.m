
#import "TreeViewController.h"
#import "TreeViewCell.h"
#import "MyButton.h"
#import "Consts.h"
#import "Task.h"

@implementation TreeViewCell

@synthesize indentLevel;
@synthesize task;
@synthesize treeViewController;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
		// cell's title label
		taskContentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		taskContentLabel.backgroundColor = [UIColor clearColor];
		taskContentLabel.opaque = NO;
		taskContentLabel.highlightedTextColor = [UIColor whiteColor];
		taskContentLabel.font = [UIFont systemFontOfSize:CONTENT_FONT_SIZE - 1];
		
		//taskContentLabel.lineBreakMode = UILineBreakModeWordWrap;
		taskContentLabel.numberOfLines = 1;
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
		treeNodeButton = [MyButton buttonWithType:UIButtonTypeCustom]; 
		treeNodeButton.frame = CGRectZero;
		treeNodeButton.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
		treeNodeButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
		[treeNodeButton addTarget:self action:@selector(openNodeAction:) forControlEvents:UIControlEventTouchDown];
		treeNodeButton.backgroundColor = [UIColor clearColor];
		[self.contentView addSubview:treeNodeButton];	
        
        self.indentationWidth = 15;
        
    }
    return self;
}

- (UIImage *)loadCheckedImage {
	return [UIImage imageNamed:@"checkedbox.png"];
}

- (UIImage *)loadUncheckedImage {
    return [UIImage imageNamed:@"box.png"];
}

- (UIImage *)loadPlusImage {
    return [UIImage imageNamed:@"toc-plus.png"];
}

- (UIImage *)loadMinusImage {
    return [UIImage imageNamed:@"toc-minus.png"];
}

- (UIImage *)loadBlankImage {
    return [UIImage imageNamed:@"toc-blank.png"];
}

- (void)openNodeAction:(id)sender {
    if (task.open) {
        [treeNodeButton setBackgroundImage:[self loadPlusImage] forState:UIControlStateNormal];
        [treeViewController closeSubTasks:task];
        task.open = NO;
    } else {
        [treeNodeButton setBackgroundImage:[self loadMinusImage] forState:UIControlStateNormal];
        [treeViewController openSubTasks:task];
        task.open = YES;
    }
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
		CGRect frame = CGRectMake(contentWidth - size.width - 4, contentRect.origin.y + 3, 
								  size.width, contentRect.size.height);
		subTaskCountLabel.frame = frame;
		titleFrameWidth = contentWidth - size.width - 8 - 20;
	} else {
		subTaskCountLabel.frame = CGRectZero;
		subTaskCountLabel.text = @"";
		titleFrameWidth = contentWidth - 20;
	}
	
	
	titleFrame = CGRectMake(contentRect.origin.x + 23, contentRect.origin.y + 3, 
							titleFrameWidth - 8, contentRect.size.height);
	
	taskContentLabel.frame = titleFrame;
	
	[self updateTaskLabelColor];
}

- (void)layoutSubviews {
    self.indentationLevel = self.indentLevel;
    [super layoutSubviews];
    CGRect contentRect = [self.contentView bounds];
	
	// layout the check button image
	CGRect frame = CGRectMake(contentRect.origin.x, 
							  contentRect.origin.y + (contentRect.size.height - CHECK_BUTTON_SIZE) / 2, 
							  20, CHECK_BUTTON_SIZE);
	treeNodeButton.frame = frame;
	
	UIImage *image;
    if (task.taskId == -1) {
        image = [self loadMinusImage];
        [treeNodeButton setEnabled:NO];
    } else {
        if (task.subTasksCount > 0) {
            if (task.open) {
                image = [self loadMinusImage];
            } else {
                image = [self loadPlusImage];
            }
            [treeNodeButton setEnabled:YES];
        } else {
            image = [self loadBlankImage];
            [treeNodeButton setEnabled:NO];
        }
    }
	[treeNodeButton setBackgroundImage:image forState:UIControlStateNormal];
	
	taskContentLabel.text = task.content;
    
    [self layoutLabels];	
}

@end
