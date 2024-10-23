#pragma once

#include <react/renderer/components/RCTTabView/RCTTabViewShadowNode.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include "RCTTabViewMeasurementsManager.h"

namespace facebook
{
    namespace react
    {

        class RCTTabViewComponentDescriptor final
            : public ConcreteComponentDescriptor<RCTTabViewShadowNode>
        {
        public:
            RCTTabViewComponentDescriptor(const ComponentDescriptorParameters &parameters)
                : ConcreteComponentDescriptor(parameters), measurementsManager_(
                                                               std::make_shared<RCTTabViewMeasurementsManagerNew>(contextContainer_)) {}

            void adopt(ShadowNode &shadowNode) const override
            {
                ConcreteComponentDescriptor::adopt(shadowNode);

                auto &rctTabViewShadowNode =
                    static_cast<RCTTabViewShadowNode &>(shadowNode);

                // `RCTTabViewShadowNode` uses `RCTTabViewMeasurementsManager` to
                // provide measurements to Yoga.
                rctTabViewShadowNode.setSliderMeasurementsManager(
                    measurementsManager_);

                // All `RCTTabViewShadowNode`s must have leaf Yoga nodes with properly
                // setup measure function.
                rctTabViewShadowNode.enableMeasurement();
            }

        private:
            const std::shared_ptr<RCTTabViewMeasurementsManagerNew> measurementsManager_;
        };

    }
}