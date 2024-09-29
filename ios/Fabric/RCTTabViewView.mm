#ifdef RCT_NEW_ARCH_ENABLED
#import "RCTTabViewView.h"

#import <react/renderer/components/RCTTabViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RCTTabViewSpec/EventEmitters.h>
#import <react/renderer/components/RCTTabViewSpec/Props.h>
#import <react/renderer/components/RCTTabViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"
#import "Utils.h"

using namespace facebook::react;

@interface RCTTabViewView () <RCTSwiftuiTabviewViewViewProtocol>

@end

@implementation SwiftuiTabviewView {
    UIView * _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<SwiftuiTabviewViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const SwiftuiTabviewViewProps>();
    _props = defaultProps;

    _view = [[UIView alloc] init];

    self.contentView = _view;
  }

  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<SwiftuiTabviewViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<SwiftuiTabviewViewProps const>(props);

    if (oldViewProps.color != newViewProps.color) {
        NSString * colorToConvert = [[NSString alloc] initWithUTF8String: newViewProps.color.c_str()];
        [_view setBackgroundColor: [Utils hexStringToColor:colorToConvert]];
    }

    [super updateProps:props oldProps:oldProps];
}

Class<RCTComponentViewProtocol> RCTTabViewViewCls(void)
{
    return RCTTabViewView.class;
}

@end
#endif
