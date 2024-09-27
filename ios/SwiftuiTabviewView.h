// This guard prevent this file to be compiled in the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

#ifndef SwiftuiTabviewViewNativeComponent_h
#define SwiftuiTabviewViewNativeComponent_h

NS_ASSUME_NONNULL_BEGIN

@interface SwiftuiTabviewView : RCTViewComponentView
@end

NS_ASSUME_NONNULL_END

#endif /* SwiftuiTabviewViewNativeComponent_h */
#endif /* RCT_NEW_ARCH_ENABLED */
