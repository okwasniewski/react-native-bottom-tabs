#ifdef __cplusplus

#pragma once

#include <jsi/jsi.h>
#include <react/renderer/components/rncore/EventEmitters.h>
#include <react/renderer/components/RNCTabView/RNCTabViewState.h>
#include <react/renderer/components/RNCTabView/Props.h>
#include <react/renderer/components/RNCTabView/EventEmitters.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>

#include "RNCTabViewMeasurementsManager.h"

namespace facebook::react {

JSI_EXPORT extern const char RNCTabViewComponentName[];

/*
* `ShadowNode` for <RNCTabView> component.
*/
class JSI_EXPORT RNCTabViewShadowNode final
: public ConcreteViewShadowNode<
RNCTabViewComponentName,
RNCTabViewProps,
RNCTabViewEventEmitter,
RNCTabViewState>
{
public:
  using ConcreteViewShadowNode::ConcreteViewShadowNode;

#ifdef ANDROID
  void setSliderMeasurementsManager(
    const std::shared_ptr<RNCTabViewMeasurementsManager> &measurementsManager);

  #pragma mark - LayoutableShadowNode

  Size measureContent(
    const LayoutContext &layoutContext,
    const LayoutConstraints &layoutConstraints) const override;

private:
  std::shared_ptr<RNCTabViewMeasurementsManager> measurementsManager_;
#endif

};

}

#endif
