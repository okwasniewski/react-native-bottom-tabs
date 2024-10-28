#ifdef __cplusplus

#pragma once

#include <react/renderer/components/RNCTabView/RNCTabViewShadowNode.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>

namespace facebook::react {

class RNCTabViewComponentDescriptor final : public ConcreteComponentDescriptor<RNCTabViewShadowNode>
{
#ifdef ANDROID
public:
  RNCTabViewComponentDescriptor(const ComponentDescriptorParameters &parameters)
    : ConcreteComponentDescriptor(parameters), measurementsManager_(
    std::make_shared<RNCTabViewMeasurementsManager>(contextContainer_)) {}

  void adopt(ShadowNode &shadowNode) const override
  {
    ConcreteComponentDescriptor::adopt(shadowNode);

    auto &rncTabViewShadowNode =
      static_cast<RNCTabViewShadowNode &>(shadowNode);

    // `RNCTabViewShadowNode` uses `RNCTabViewMeasurementsManager` to
    // provide measurements to Yoga.
    rncTabViewShadowNode.setSliderMeasurementsManager(
      measurementsManager_);

    // All `RNCTabViewShadowNode`s must have leaf Yoga nodes with properly
    // setup measure function.
    rncTabViewShadowNode.enableMeasurement();
  }

private:
  const std::shared_ptr<RNCTabViewMeasurementsManager> measurementsManager_;
#else
public:
  RNCTabViewComponentDescriptor(const ComponentDescriptorParameters &parameters)
  : ConcreteComponentDescriptor(parameters) {}
#endif

};

}

#endif
