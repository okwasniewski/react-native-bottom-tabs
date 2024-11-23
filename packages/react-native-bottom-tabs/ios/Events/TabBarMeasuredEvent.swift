import React

@objc public class TabBarMeasuredEvent: NSObject, RCTEvent {
  private var height: NSInteger
  @objc public var viewTag: NSNumber
  @objc public var coalescingKey: UInt16
  
  @objc public var eventName: String {
    return "onTabBarMeasured"
  }
  
  @objc public init(reactTag: NSNumber, height: NSInteger, coalescingKey: UInt16) {
    self.viewTag = reactTag
    self.height = height
    self.coalescingKey = coalescingKey
    super.init()
  }
  
  @objc public func canCoalesce() -> Bool {
    return false
  }
  
  @objc public class func moduleDotMethod() -> String {
    return "RCTEventEmitter.receiveEvent"
  }
  
  @objc public func arguments() -> [Any] {
    return [
      viewTag,
      RCTNormalizeInputEventName(eventName) ?? eventName,
      [
        "height": height
      ]
    ]
  }
}
