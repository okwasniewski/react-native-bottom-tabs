#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"
#import "react_native_bottom_tabs-Swift.h"

@interface RCTTabView : RCTViewManager
@end

@implementation RCTTabView

RCT_EXPORT_MODULE(RCTTabView)

- (UIView *)view
{
  return [[TabViewProvider alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

RCT_EXPORT_VIEW_PROPERTY(items, NSArray)
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(selectedPage, NSString)
RCT_EXPORT_VIEW_PROPERTY(tabViewStyle, NSString)

@end
