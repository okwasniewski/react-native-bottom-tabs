import React

@objcMembers
public class OnNativeLayoutEvent: NSObject, RCTEvent {
  private var size: CGSize
  public var viewTag: NSNumber

  public var eventName: String {
    return "onNativeLayout"
  }

  public init(reactTag: NSNumber, size: CGSize) {
    self.viewTag = reactTag
    self.size = size
    super.init()
  }

  public class func moduleDotMethod() -> String {
    return "RCTEventEmitter.receiveEvent"
  }
  
  public func canCoalesce() -> Bool {
      return false
  }

  public func arguments() -> [Any] {
    return [
      viewTag,
      RCTNormalizeInputEventName(eventName) ?? eventName,
      [
        "width": size.width,
        "height": size.height
      ]
    ]
  }
}
