import React


// RCTEvent is not defined for new arch.
protocol RCTEvent {}

@objcMembers
public class TabLongPressEvent: NSObject, RCTEvent {
  private var key: NSString
  public var viewTag: NSNumber
  
  public var eventName: String {
    return "onTabLongPress"
  }
  
  public init(reactTag: NSNumber, key: NSString) {
    self.viewTag = reactTag
    self.key = key
    super.init()
  }
  
  public class func moduleDotMethod() -> String {
    return "RCTEventEmitter.receiveEvent"
  }
  
  public func arguments() -> [Any] {
    return [
      viewTag,
      RCTNormalizeInputEventName(eventName) ?? eventName,
      [
        "key": key
      ]
    ]
  }
}
