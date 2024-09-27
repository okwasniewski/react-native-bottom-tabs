import React

class PageSelectedEvent: NSObject, RCTEvent {
  var viewTag: NSNumber
  private var key: NSString
  internal var coalescingKey: UInt16
  
  var eventName: String {
    return "onPageSelected"
  }
  
  init(reactTag: NSNumber, key: NSString, coalescingKey: UInt16) {
    self.viewTag = reactTag
    self.key = key
    self.coalescingKey = coalescingKey
    super.init()
  }
  
  func canCoalesce() -> Bool {
    return false
  }
  
  class func moduleDotMethod() -> String {
    return "RCTEventEmitter.receiveEvent"
  }
  
  func arguments() -> [Any] {
    return [
      viewTag,
      RCTNormalizeInputEventName(eventName),
      [
        "key": key
      ]
    ]
  }
}
