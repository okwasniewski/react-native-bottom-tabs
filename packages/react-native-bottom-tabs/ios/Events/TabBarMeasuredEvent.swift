import React

@objcMembers
public class TabBarMeasuredEvent: NSObject, RCTEvent {
  private var height: NSInteger
  public var viewTag: NSNumber
  
  public var eventName: String {
    return "onTabBarMeasured"
  }
  
  public init(reactTag: NSNumber, height: NSInteger) {
    self.viewTag = reactTag
    self.height = height
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
        "height": height
      ]
    ]
  }
}
