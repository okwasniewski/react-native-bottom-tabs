#pragma once

#include <jsi/jsi.h>
#include <react/renderer/components/rncore/EventEmitters.h>
#include <react/renderer/components/RCTTabView/RCTTabViewState.h>
#include <react/renderer/components/RCTTabView/Props.h>
#include <react/renderer/components/RCTTabView/EventEmitters.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>

#include "RCTTabViewMeasurementsManager.h"

namespace facebook
{
    namespace react
    {

        JSI_EXPORT extern const char RCTTabViewComponentName[];

        /*
         * `ShadowNode` for <RCTTabView> component.
         */
        class JSI_EXPORT RCTTabViewShadowNode final
            : public ConcreteViewShadowNode<
                  RCTTabViewComponentName,
                  RCTTabViewProps,
                  RCTTabViewEventEmitter,
                  RCTTabViewState>
        {
        public:
            using ConcreteViewShadowNode::ConcreteViewShadowNode;

            void setSliderMeasurementsManager(
                const std::shared_ptr<RCTTabViewMeasurementsManagerNew> &measurementsManager);

#pragma mark - LayoutableShadowNode

            Size measureContent(
                const LayoutContext &layoutContext,
                const LayoutConstraints &layoutConstraints) const override;

        private:
            std::shared_ptr<RCTTabViewMeasurementsManagerNew> measurementsManager_;
        };

    }
}