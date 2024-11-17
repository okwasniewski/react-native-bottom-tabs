#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTImageLoader.h>
#import "RCTBridge.h"

#if __has_include("react_native_bottom_tabs/react_native_bottom_tabs-Swift.h")
#import "react_native_bottom_tabs/react_native_bottom_tabs-Swift.h"
#else
#import "react_native_bottom_tabs-Swift.h"
#endif

@interface RCTTabView : RCTViewManager <TabViewProviderDelegate>
@end

@implementation RCTTabView {
  uint16_t _coalescingKey;
}

RCT_EXPORT_MODULE(RNCTabView)

- (instancetype)init
{
  self = [super init];
  if (self) {
    _coalescingKey = 0;
  }
  return self;
}

RCT_EXPORT_VIEW_PROPERTY(items, NSArray)
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onTabLongPress, RCTDirectEventBlock)
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
  auto event = [[TabLongPressEvent alloc] initWithReactTag:reactTag key:key coalescingKey:_coalescingKey++];
  [self.bridge.eventDispatcher sendEvent:event];
}

- (void)onPageSelectedWithKey:(NSString *)key reactTag:(NSNumber *)reactTag {
  auto event = [[PageSelectedEvent alloc] initWithReactTag:reactTag key:key coalescingKey:_coalescingKey++];
  [self.bridge.eventDispatcher sendEvent:event];
}

- (UIView *)view
{
  return [[TabViewProvider alloc] initWithDelegate:self];
}

@end
