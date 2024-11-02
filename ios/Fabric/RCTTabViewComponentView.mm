#ifdef RCT_NEW_ARCH_ENABLED
#import "RCTTabViewComponentView.h"

#import <react/renderer/components/RNCTabView/ComponentDescriptors.h>
#import <react/renderer/components/RNCTabView/RNCTabViewComponentDescriptor.h>
#import <react/renderer/components/RNCTabView/EventEmitters.h>
#import <react/renderer/components/RNCTabView/Props.h>
#import <react/renderer/components/RNCTabView/RCTComponentViewHelpers.h>

#import <React/RCTFabricComponentsPlugins.h>

#if __has_include("react_native_bottom_tabs/react_native_bottom_tabs-Swift.h")
#import "react_native_bottom_tabs/react_native_bottom_tabs-Swift.h"
#else
#import "react_native_bottom_tabs-Swift.h"
#endif

#import <React/RCTImageLoader.h>
#import <React/RCTImageSource.h>
#import <React/RCTBridge+Private.h>
#import "RCTImagePrimitivesConversions.h"
#import "RCTConversions.h"


using namespace facebook::react;

@interface RCTTabViewComponentView () <RCTRNCTabViewViewProtocol, TabViewProviderDelegate> {
}

@end

@implementation RCTTabViewComponentView {
  TabViewProvider *_tabViewProvider;
  NSMutableArray<UIView *> *_reactSubviews;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCTabViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCTabViewProps>();
    _reactSubviews = [NSMutableArray new];
    RCTImageLoader *imageLoader = [[RCTBridge currentBridge] moduleForName:@"ImageLoader"];
    _tabViewProvider = [[TabViewProvider alloc] initWithDelegate:self imageLoader:imageLoader];
    self.contentView = _tabViewProvider;
    _props = defaultProps;
  }

  return self;
}

// Opt out of recycling for now, it's not working properly.
+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)layoutSubviews {
  [super layoutSubviews];
  _tabViewProvider.children = [self reactSubviews];
}

- (NSArray<UIView *> *)reactSubviews
{
  return _reactSubviews;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  [_reactSubviews insertObject:childComponentView atIndex:index];
  _tabViewProvider.children = [self reactSubviews];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  [_reactSubviews removeObjectAtIndex:index];

  [childComponentView removeFromSuperview];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<RNCTabViewProps const>(_props);
  const auto &newViewProps = *std::static_pointer_cast<RNCTabViewProps const>(props);

  if (haveTabItemsChanged(oldViewProps.items, newViewProps.items)) {
    _tabViewProvider.itemsData = convertItemsToArray(newViewProps.items);
  }

  if (oldViewProps.translucent != newViewProps.translucent) {
    _tabViewProvider.translucent = newViewProps.translucent;
  }

  if (oldViewProps.icons != newViewProps.icons) {
    auto iconsArray = [[NSMutableArray alloc] init];
    for (auto &source: newViewProps.icons) {
      auto imageSource = [[RCTImageSource alloc] initWithURLRequest:NSURLRequestFromImageSource(source) size:CGSizeMake(source.size.width, source.size.height) scale:source.scale];
      [iconsArray addObject:imageSource];
    }

    _tabViewProvider.icons = iconsArray;
  }

  if (oldViewProps.sidebarAdaptable != newViewProps.sidebarAdaptable) {
    _tabViewProvider.sidebarAdaptable = newViewProps.sidebarAdaptable;
  }

  if (oldViewProps.disablePageAnimations != newViewProps.disablePageAnimations) {
    _tabViewProvider.disablePageAnimations = newViewProps.disablePageAnimations;
  }

  if (oldViewProps.labeled != newViewProps.labeled) {
    _tabViewProvider.labeled = newViewProps.labeled;
  }

  if (oldViewProps.ignoresTopSafeArea != newViewProps.ignoresTopSafeArea) {
    _tabViewProvider.ignoresTopSafeArea = newViewProps.ignoresTopSafeArea;
  }

  if (oldViewProps.selectedPage != newViewProps.selectedPage) {
    _tabViewProvider.selectedPage = RCTNSStringFromString(newViewProps.selectedPage);
  }

  if (oldViewProps.scrollEdgeAppearance != newViewProps.scrollEdgeAppearance) {
    _tabViewProvider.scrollEdgeAppearance = RCTNSStringFromString(newViewProps.scrollEdgeAppearance);
  }

  if (oldViewProps.labeled != newViewProps.labeled) {
    _tabViewProvider.labeled = newViewProps.labeled;
  }

  if (oldViewProps.barTintColor != newViewProps.barTintColor) {
    _tabViewProvider.barTintColor = RCTUIColorFromSharedColor(newViewProps.barTintColor);
  }

  if (oldViewProps.activeTintColor != newViewProps.activeTintColor) {
    _tabViewProvider.activeTintColor = RCTUIColorFromSharedColor(newViewProps.activeTintColor);
  }

  if (oldViewProps.inactiveTintColor != newViewProps.inactiveTintColor) {
    _tabViewProvider.inactiveTintColor = RCTUIColorFromSharedColor(newViewProps.inactiveTintColor);
  }
  
  if (oldViewProps.hapticFeedbackEnabled != newViewProps.hapticFeedbackEnabled) {
    _tabViewProvider.hapticFeedbackEnabled = newViewProps.hapticFeedbackEnabled;
  }

  [super updateProps:props oldProps:oldProps];
}

bool areTabItemsEqual(const RNCTabViewItemsStruct& lhs, const RNCTabViewItemsStruct& rhs) {
  return lhs.key == rhs.key &&
  lhs.title == rhs.title &&
  lhs.sfSymbol == rhs.sfSymbol &&
  lhs.badge == rhs.badge &&
  lhs.activeTintColor == rhs.activeTintColor &&
  lhs.hidden == rhs.hidden;
}

bool haveTabItemsChanged(const std::vector<RNCTabViewItemsStruct>& oldItems,
                         const std::vector<RNCTabViewItemsStruct>& newItems) {

  if (oldItems.size() != newItems.size()) {
    return true;
  }

  for (size_t i = 0; i < oldItems.size(); ++i) {
    if (!areTabItemsEqual(oldItems[i], newItems[i])) {
      return true;
    }
  }

  return false;
}

NSArray* convertItemsToArray(const std::vector<RNCTabViewItemsStruct>& items) {
  NSMutableArray<TabInfo *> *result = [NSMutableArray array];

  for (const auto& item : items) {
    auto tabInfo = [[TabInfo alloc] initWithKey:RCTNSStringFromString(item.key) title:RCTNSStringFromString(item.title) badge:RCTNSStringFromString(item.badge) sfSymbol:RCTNSStringFromString(item.sfSymbol) activeTintColor:RCTUIColorFromSharedColor(item.activeTintColor) hidden:item.hidden];

    [result addObject:tabInfo];
  }

  return result;
}

//  MARK: TabViewProviderDelegate

- (void)onPageSelectedWithKey:(NSString *)key reactTag:(NSNumber *)reactTag {
  auto eventEmitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(_eventEmitter);
  if (eventEmitter) {
    eventEmitter->onPageSelected(RNCTabViewEventEmitter::OnPageSelected{
      .key = [key cStringUsingEncoding:kCFStringEncodingUTF8]
    });
  }
}

- (void)onLongPressWithKey:(NSString *)key reactTag:(NSNumber *)reactTag {
  auto eventEmitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(_eventEmitter);
  if (eventEmitter) {
    eventEmitter->onTabLongPress(RNCTabViewEventEmitter::OnTabLongPress {
      .key = [key cStringUsingEncoding:kCFStringEncodingUTF8]
    });
  }
}

@end

Class<RCTComponentViewProtocol> RNCTabViewCls(void)
{
  return RCTTabViewComponentView.class;
}

#endif // RCT_NEW_ARCH_ENABLED


