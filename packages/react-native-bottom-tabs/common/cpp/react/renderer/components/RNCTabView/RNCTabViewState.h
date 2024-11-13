#ifdef __cplusplus

#pragma once

#ifdef ANDROID
#include <folly/dynamic.h>
#include <react/renderer/mapbuffer/MapBuffer.h>
#include <react/renderer/mapbuffer/MapBufferBuilder.h>
#endif

namespace facebook::react {

class RNCTabViewState
{
public:
  RNCTabViewState() = default;
  
#ifdef ANDROID
  RNCTabViewState(RNCTabViewState const &previousState, folly::dynamic data) {};
  
  folly::dynamic getDynamic() const
  {
    return {};
  };
  
  MapBuffer getMapBuffer() const
  {
    return MapBufferBuilder::EMPTY();
  };
#endif
};

}

#endif
