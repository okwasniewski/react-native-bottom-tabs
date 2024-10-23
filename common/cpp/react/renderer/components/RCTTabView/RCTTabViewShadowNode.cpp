#include "RCTTabViewShadowNode.h"
#include "RCTTabViewMeasurementsManager.h"

namespace facebook
{
    namespace react
    {

        extern const char RCTTabViewComponentName[] = "RCTTabView";

        void RCTTabViewShadowNode::setSliderMeasurementsManager(
            const std::shared_ptr<RCTTabViewMeasurementsManagerNew> &
                measurementsManager)
        {
            ensureUnsealed();
            measurementsManager_ = measurementsManager;
        }

#pragma mark - LayoutableShadowNode

        Size RCTTabViewShadowNode::measureContent(
            const LayoutContext & /*layoutContext*/,
            const LayoutConstraints &layoutConstraints) const
        {
            return measurementsManager_->measure(getSurfaceId(), layoutConstraints);
        }

    }
}