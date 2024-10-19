#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTImageLoader.h>
#import <React/RCTBridge.h>
#if TARGET_OS_OSX
#import <React/RCTUIKit.h>
#endif

#if TARGET_OS_OSX
#import <AppKit/AppKit.h>
#else
#import <UIKit/UIKit.h>
#endif

#if __has_include("react_native_bottom_tabs/react_native_bottom_tabs-Swift.h")
#import "react_native_bottom_tabs/react_native_bottom_tabs-Swift.h"
#else
#import "react_native_bottom_tabs-Swift.h"
#endif

@interface RCTTabView : RCTViewManager
@end

@implementation RCTTabView

RCT_EXPORT_MODULE(RCTTabView)

#if TARGET_OS_OSX
- (NSView *)view
#else
- (UIView *) view
#endif
{
  RCTImageLoader *imageLoader = [self.bridge moduleForClass:[RCTImageLoader class]];
  return [[TabViewProvider alloc] initWithEventDispatcher:self.bridge.eventDispatcher imageLoader:imageLoader];
}

RCT_EXPORT_VIEW_PROPERTY(items, NSArray)
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(selectedPage, NSString)
RCT_EXPORT_VIEW_PROPERTY(tabViewStyle, NSString)
RCT_EXPORT_VIEW_PROPERTY(icons, NSArray<RCTImageSource *>);
RCT_EXPORT_VIEW_PROPERTY(sidebarAdaptable, BOOL)
RCT_EXPORT_VIEW_PROPERTY(labeled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(ignoresTopSafeArea, BOOL)
RCT_EXPORT_VIEW_PROPERTY(disablePageAnimations, BOOL)
RCT_EXPORT_VIEW_PROPERTY(scrollEdgeAppearance, NSString)

@end
