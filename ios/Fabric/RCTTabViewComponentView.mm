#ifdef RCT_NEW_ARCH_ENABLED
#import "RCTTabViewComponentView.h"

#import <react/renderer/components/RNCTabViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNCTabViewSpec/EventEmitters.h>
#import <react/renderer/components/RNCTabViewSpec/Props.h>
#import <react/renderer/components/RNCTabViewSpec/RCTComponentViewHelpers.h>

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
    // FIXME: This works but it's not the best solution
    RCTImageLoader *imageLoader = [[RCTBridge currentBridge] moduleForName:@"ImageLoader"];
    _tabViewProvider = [[TabViewProvider alloc] initWithDelegate:self imageLoader:imageLoader];
    _reactSubviews = [NSMutableArray new];
    self.contentView = _tabViewProvider;
    _props = defaultProps;
  }

  return self;
}

- (void)prepareForRecycle {
  [super prepareForRecycle];
  _reactSubviews = [NSMutableArray new];
  self.contentView = nil;
  _tabViewProvider = nil;
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
  
  if (oldViewProps.translucent != newViewProps.translucent) {
    _tabViewProvider.translucent = newViewProps.translucent;
  }
  
  if (oldViewProps.icons != newViewProps.icons) {
    auto iconsArray = [[NSMutableArray alloc] init];
    for (auto &source : newViewProps.icons) {
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
    _tabViewProvider.selectedPage = [NSString stringWithCString:toString(newViewProps.selectedPage).c_str() encoding:NSASCIIStringEncoding];
  }
  
  if (oldViewProps.scrollEdgeAppearance != newViewProps.scrollEdgeAppearance) {
    _tabViewProvider.scrollEdgeAppearance = [NSString stringWithCString:toString(newViewProps.scrollEdgeAppearance).c_str() encoding:NSASCIIStringEncoding];
  }
  
  if (oldViewProps.labeled != newViewProps.labeled) {
    _tabViewProvider.labeled = newViewProps.labeled;
  }
  
  // FIXME: This only compares size changes because RNCTabViewItemsStruct doesn't have `==` comparison implemented.
  if (oldViewProps.items.size() != newViewProps.items.size()) {
    _tabViewProvider.items = convertItemsToArray(newViewProps.items);
  }

  [super updateProps:props oldProps:oldProps];
}


NSArray* convertItemsToArray(const std::vector<RNCTabViewItemsStruct>& items) {
    NSMutableArray *result = [NSMutableArray array];
    
    for (const auto& item : items) {
        NSDictionary *dict = @{
            @"key": [NSString stringWithUTF8String:item.key.c_str()],
            @"title": [NSString stringWithUTF8String:item.title.c_str()],
            @"sfSymbol": [NSString stringWithUTF8String:item.sfSymbol.c_str()],
            @"badge": [NSString stringWithUTF8String:item.badge.c_str()]
        };
        
        [result addObject:dict];
    }
    
    return result;
}


//  MARK: TabViewProviderDelegate

- (void)onPageSelectedWithKey:(NSString *)key {
  auto eventEmitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(_eventEmitter);
  if (eventEmitter) {
    eventEmitter->onPageSelected(RNCTabViewEventEmitter::OnPageSelected{
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


