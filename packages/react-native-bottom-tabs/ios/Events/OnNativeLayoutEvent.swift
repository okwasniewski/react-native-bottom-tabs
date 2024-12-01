import React

@objc public class OnNativeLayoutEvent: NSObject, RCTEvent {
  private var size: CGSize
  @objc public var viewTag: NSNumber
  @objc public var coalescingKey: UInt16

  @objc public var eventName: String {
    return "onNativeLayout"
  }

  @objc public init(reactTag: NSNumber, size: CGSize, coalescingKey: UInt16) {
    self.viewTag = reactTag
    self.size = size
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
        "width": size.width,
        "height": size.height
      ]
    ]
  }
}
