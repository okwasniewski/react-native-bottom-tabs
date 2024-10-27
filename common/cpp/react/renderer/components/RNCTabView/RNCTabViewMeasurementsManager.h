#ifdef __cplusplus

#ifdef ANDROID
#pragma once
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include <react/renderer/core/LayoutConstraints.h>
#include <react/utils/ContextContainer.h>

namespace facebook::react
{

    class RNCTabViewMeasurementsManager
    {
    public:
        RNCTabViewMeasurementsManager(
            const ContextContainer::Shared &contextContainer)
            : contextContainer_(contextContainer) {}

        Size measure(SurfaceId surfaceId, LayoutConstraints layoutConstraints) const;

    private:
        const ContextContainer::Shared contextContainer_;
        mutable std::mutex mutex_;
        mutable bool hasBeenMeasured_ = false;
        mutable Size cachedMeasurement_{};
    };
}

#endif

#endif
