#ifdef RCT_NEW_ARCH_ENABLED
#import "SwiftuiTabviewView.h"

#import <react/renderer/components/RNSwiftuiTabviewViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNSwiftuiTabviewViewSpec/EventEmitters.h>
#import <react/renderer/components/RNSwiftuiTabviewViewSpec/Props.h>
#import <react/renderer/components/RNSwiftuiTabviewViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"
#import "Utils.h"

using namespace facebook::react;

@interface SwiftuiTabviewView () <RCTSwiftuiTabviewViewViewProtocol>

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

Class<RCTComponentViewProtocol> SwiftuiTabviewViewCls(void)
{
    return SwiftuiTabviewView.class;
}

@end
#endif
