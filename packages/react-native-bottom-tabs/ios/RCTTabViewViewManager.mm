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

@interface RCTTabView : RCTViewManager <TabViewProviderDelegate>
@end

@implementation RCTTabView

RCT_EXPORT_MODULE(RNCTabView)

RCT_EXPORT_VIEW_PROPERTY(items, NSArray)
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onTabLongPress, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onTabBarMeasured, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onNativeLayout, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(selectedPage, NSString)
RCT_EXPORT_VIEW_PROPERTY(tabViewStyle, NSString)
RCT_EXPORT_VIEW_PROPERTY(icons, NSArray<RCTImageSource *>);
RCT_EXPORT_VIEW_PROPERTY(sidebarAdaptable, BOOL)
RCT_EXPORT_VIEW_PROPERTY(labeled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(ignoresTopSafeArea, BOOL)
RCT_EXPORT_VIEW_PROPERTY(disablePageAnimations, BOOL)
RCT_EXPORT_VIEW_PROPERTY(scrollEdgeAppearance, NSString)
RCT_EXPORT_VIEW_PROPERTY(translucent, BOOL)
RCT_EXPORT_VIEW_PROPERTY(barTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(activeTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(inactiveTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(hapticFeedbackEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(fontFamily, NSString)
RCT_EXPORT_VIEW_PROPERTY(fontWeight, NSString)
RCT_EXPORT_VIEW_PROPERTY(fontSize, NSNumber)

//  MARK: TabViewProviderDelegate

- (void)onLongPressWithKey:(NSString *)key reactTag:(NSNumber *)reactTag {
  auto event = [[TabLongPressEvent alloc] initWithReactTag:reactTag key:key];
  [self.bridge.eventDispatcher sendEvent:event];
}

- (void)onPageSelectedWithKey:(NSString *)key reactTag:(NSNumber *)reactTag {
  auto event = [[PageSelectedEvent alloc] initWithReactTag:reactTag key:key];
  [self.bridge.eventDispatcher sendEvent:event];
}

- (void)onTabBarMeasuredWithHeight:(NSInteger)height reactTag:(NSNumber *)reactTag {
  auto event = [[TabBarMeasuredEvent alloc] initWithReactTag:reactTag height:height];
  [self.bridge.eventDispatcher sendEvent:event];
}

- (void)onLayoutWithSize:(CGSize)size reactTag:(NSNumber *)reactTag {
  auto event = [[OnNativeLayoutEvent alloc] initWithReactTag:reactTag size:size];
  [self.bridge.eventDispatcher sendEvent:event];
}

#if TARGET_OS_OSX
- (NSView *)view
#else
- (UIView *) view
#endif
{
  return [[TabViewProvider alloc] initWithDelegate:self];
}

@end
