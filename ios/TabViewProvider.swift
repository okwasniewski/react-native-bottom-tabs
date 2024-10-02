import Foundation
import SwiftUI
import React

struct TabInfo: Codable {
  let key: String
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
  private var eventDispatcher: RCTEventDispatcherProtocol?
  private var imageLoader: RCTImageLoaderProtocol?
  private var iconSize = CGSize(width: 25, height: 25)
  
  
  @objc var onPageSelected: RCTDirectEventBlock?
  
  @objc var icons: NSArray? {
    didSet {
      loadIcons(icons)
    }
  }
  
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
  
  @objc public convenience init(eventDispatcher: RCTEventDispatcherProtocol, imageLoader: RCTImageLoader) {
    self.init()
    self.eventDispatcher = eventDispatcher
    self.imageLoader = imageLoader
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
  
  private func loadIcons(_ icons: NSArray?) {
    // TODO: Diff the arrays and update only changed items.
    // Now if the user passes `unfocusedIcon` we update every item.
    if let imageSources = icons as? [RCTImageSource?] {
      for (index, imageSource) in imageSources.enumerated() {
        guard let imageSource, let imageLoader else { continue }
        imageLoader.loadImage(
          with: imageSource.request,
          size: iconSize,
          scale: imageSource.scale,
          clipped: true,
          resizeMode: RCTResizeMode.contain,
          progressBlock: { _,_ in },
          partialLoad: { _ in },
          completionBlock: { error, image in
            if error != nil {
              print("[TabView] Error loading image: \(error!.localizedDescription)")
              return
            }
            guard let image else { return }
            DispatchQueue.main.async {
              self.props.icons[index] = image
            }
          })
      }
    }
  }
  
  private func parseTabData(from array: NSArray?) -> TabData? {
    guard let array else { return nil }
    var items: [TabInfo] = []
    
    for value in array {
      if let itemDict = value as? [String: Any] {
        items.append(
          TabInfo(
            key: itemDict["key"] as? String ?? "",
            title: itemDict["title"] as? String ?? "",
            badge: itemDict["badge"] as? String ?? ""
          )
        )
      }
    }
    
    return TabData(tabs: items)
  }
}
