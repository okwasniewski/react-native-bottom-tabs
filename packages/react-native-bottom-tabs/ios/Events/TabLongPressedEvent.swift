import React


// RCTEvent is not defined for new arch.
protocol RCTEvent {}

@objc public class TabLongPressEvent: NSObject, RCTEvent {
  private var key: NSString
  @objc public var viewTag: NSNumber
  @objc public var coalescingKey: UInt16
  
  @objc public var eventName: String {
    return "onTabLongPress"
  }
  
  @objc public init(reactTag: NSNumber, key: NSString, coalescingKey: UInt16) {
    self.viewTag = reactTag
    self.key = key
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
        "key": key
      ]
    ]
  }
}
