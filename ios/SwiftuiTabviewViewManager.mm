#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"
#import "react_native_swiftui_tabview-Swift.h"

@interface SwiftUITabViewViewManager : RCTViewManager
@end

@implementation SwiftUITabViewViewManager

RCT_EXPORT_MODULE(SwiftUITabViewView)

- (UIView *)view
{
  return [[TabViewProvider alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

RCT_EXPORT_VIEW_PROPERTY(items, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(selectedPage, NSString)

@end
