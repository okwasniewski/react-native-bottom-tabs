#pragma once

#include <folly/dynamic.h>
#include <react/renderer/mapbuffer/MapBuffer.h>
#include <react/renderer/mapbuffer/MapBufferBuilder.h>

namespace facebook
{
    namespace react
    {

        class RCTTabViewState
        {
        public:
            RCTTabViewState() = default;

            RCTTabViewState(RCTTabViewState const &previousState, folly::dynamic data) {};
            folly::dynamic getDynamic() const
            {
                return {};
            };
            MapBuffer getMapBuffer() const
            {
                return MapBufferBuilder::EMPTY();
            };
        };

    }
}