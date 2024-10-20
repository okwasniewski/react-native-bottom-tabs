import React


// RCTEvent is not defined for new arch.
protocol RCTEvent {}

@objc public class TabLongPressEvent: NSObject, RCTEvent {
  public var viewTag: NSNumber
  private var key: NSString
  public var coalescingKey: UInt16

  public var eventName: String {
    return "onTabLongPress"
  }

  @objc public init(reactTag: NSNumber, key: NSString, coalescingKey: UInt16) {
    self.viewTag = reactTag
    self.key = key
    self.coalescingKey = coalescingKey
    super.init()
  }

  public func canCoalesce() -> Bool {
    return false
  }

  public class func moduleDotMethod() -> String {
    return "RCTEventEmitter.receiveEvent"
  }

  public func arguments() -> [Any] {
    return [
      viewTag,
      RCTNormalizeInputEventName(eventName),
      [
        "key": key
      ]
    ]
  }
}
