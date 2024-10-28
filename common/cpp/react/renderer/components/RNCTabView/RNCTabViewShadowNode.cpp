#include "RNCTabViewShadowNode.h"
#include "RNCTabViewMeasurementsManager.h"

namespace facebook::react {

extern const char RNCTabViewComponentName[] = "RNCTabView";

#ifdef ANDROID
void RNCTabViewShadowNode::setSliderMeasurementsManager(
  const std::shared_ptr<RNCTabViewMeasurementsManager> &
  measurementsManager)
{
  ensureUnsealed();
  measurementsManager_ = measurementsManager;
}

#pragma mark - LayoutableShadowNode

Size RNCTabViewShadowNode::measureContent(
  const LayoutContext & /*layoutContext*/,
  const LayoutConstraints &layoutConstraints) const
{
  return measurementsManager_->measure(getSurfaceId(), layoutConstraints);
}

#endif

}
