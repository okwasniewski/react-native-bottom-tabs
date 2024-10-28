#ifdef ANDROID
#include "RNCTabViewMeasurementsManager.h"

#include <fbjni/fbjni.h>
#include <react/jni/ReadableNativeMap.h>
#include <react/renderer/core/conversions.h>


using namespace facebook::jni;

namespace facebook::react
{
Size RNCTabViewMeasurementsManager::measure(
  SurfaceId surfaceId,
  LayoutConstraints layoutConstraints) const
{
  {
    std::scoped_lock lock(mutex_);
    if (hasBeenMeasured_)
    {
      return cachedMeasurement_;
    }
  }

  const jni::global_ref<jobject>& fabricUIManager =
    contextContainer_->at<jni::global_ref<jobject>>("FabricUIManager");

  static auto measure = facebook::jni::findClassStatic(
    "com/facebook/react/fabric/FabricUIManager")
    ->getMethod<jlong(
      jint,
      jstring,
      ReadableMap::javaobject,
      ReadableMap::javaobject,
      ReadableMap::javaobject,
      jfloat,
      jfloat,
      jfloat,
      jfloat)>("measure");

  auto minimumSize = layoutConstraints.minimumSize;
  auto maximumSize = layoutConstraints.maximumSize;

  local_ref<JString> componentName = make_jstring("RNCTabView");

  auto measurement = yogaMeassureToSize(measure(
    fabricUIManager,
    surfaceId,
    componentName.get(),
    nullptr,
    nullptr,
    nullptr,
    minimumSize.width,
    maximumSize.width,
    minimumSize.height,
    maximumSize.height));

  std::scoped_lock lock(mutex_);
  cachedMeasurement_ = measurement;
  hasBeenMeasured_ = true;
  return measurement;
}
}

#endif
