#pragma once

#include <ReactCommon/JavaTurboModule.h>
#include <ReactCommon/TurboModule.h>
#include <jsi/jsi.h>
#include <react/renderer/components/RNCTabView/RNCTabViewComponentDescriptor.h>

namespace facebook::react {
JSI_EXPORT
std::shared_ptr<TurboModule> RNCTabView_ModuleProvider(
  const std::string &moduleName,
  const JavaTurboModule::InitParams &params);
}
