import Foundation
import SwiftUI

struct TabInfo: Codable {
  let key: String
  let icon: String
  let title: String
  let badge: String
}

struct TabData: Codable {
  let tabs: [TabInfo]
}

@objc public class TabViewProvider: UIView {
  var props = TabViewProps()
  private var hostingController: UIHostingController<TabViewImpl>?
  private var coalescingKey: UInt16 = 0
  var eventDispatcher: RCTEventDispatcherProtocol?
  
  @objc var onPageSelected: RCTDirectEventBlock?
  @objc var selectedPage: NSString? {
    didSet {
      props.selectedPage = selectedPage as? String
    }
  }
  @objc var tabViewStyle: NSString? {
    didSet {
      props.tabViewStyle = tabViewStyle as? String
    }
  }
  @objc var items: NSArray? {
    didSet {
      props.items = parseTabData(from: items)
    }
  }
  
  @objc public convenience init(eventDispatcher: RCTEventDispatcherProtocol) {
    self.init()
    self.eventDispatcher = eventDispatcher
  }
  
  public override func didUpdateReactSubviews() {
    props.children = reactSubviews()
  }
  
  public override func layoutSubviews() {
    super.layoutSubviews()
    setupView()
    props.children = reactSubviews()
  }
  
  private func setupView() {
    if self.hostingController != nil {
      return
    }
    
    self.hostingController = UIHostingController(rootView: TabViewImpl(props: props) { key in
      self.coalescingKey += 1
      self.eventDispatcher?.send(PageSelectedEvent(reactTag: self.reactTag, key: NSString(string: key), coalescingKey: self.coalescingKey))
    })
    if let hostingController = self.hostingController, let parentViewController = findViewController() {
      parentViewController.addChild(hostingController)
      addSubview(hostingController.view)
      hostingController.view.translatesAutoresizingMaskIntoConstraints = false
      hostingController.view.pinEdges(to: self)
      hostingController.didMove(toParent: parentViewController)
    }
  }
  
  private func findViewController() -> UIViewController? {
    var responder: UIResponder? = self
    while let nextResponder = responder?.next {
      if let viewController = nextResponder as? UIViewController {
        return viewController
      }
      responder = nextResponder
    }
    return nil
  }
  
  func parseTabData(from array: NSArray?) -> TabData? {
    guard let array else { return nil }
    var items: [TabInfo] = []
    
    for value in array {
      if let itemDict = value as? [String: Any] {
        items.append(
          TabInfo(
            key: itemDict["key"] as? String ?? "",
            icon: itemDict["icon"] as? String ?? "",
            title: itemDict["title"] as? String ?? "",
            badge: itemDict["badge"] as? String ?? ""
          )
        )
      }
    }
    
    return TabData(tabs: items)
  }
}
