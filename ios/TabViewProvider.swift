import Foundation
import SwiftUI
import React

struct TabInfo: Codable {
  let key: String
  let title: String
  let badge: String
  let sfSymbol: String
}

struct TabData: Codable {
  let tabs: [TabInfo]
}

@objc public class TabViewProvider: PlatformView {
  var props = TabViewProps()
#if os(iOS)
  private var hostingController: UIHostingController<TabViewImpl>?
#elseif os(macOS)
  private var hostingController: NSHostingController<TabViewImpl>?
#endif
  private var coalescingKey: UInt16 = 0
  private var eventDispatcher: RCTEventDispatcherProtocol?
  private var imageLoader: RCTImageLoaderProtocol?
  private var iconSize = CGSize(width: 27, height: 27)
  
  @objc var onPageSelected: RCTDirectEventBlock?
  
  @objc var icons: NSArray? {
    didSet {
      loadIcons(icons)
    }
  }
  
  @objc var sidebarAdaptable: Bool = false {
    didSet {
      props.sidebarAdaptable = sidebarAdaptable
    }
  }
  
  @objc var disablePageAnimations: Bool = false {
    didSet {
      props.disablePageAnimations = disablePageAnimations
    }
  }
  
  @objc var labeled: Bool = true {
    didSet {
      props.labeled = labeled
    }
  }
  
  @objc var ignoresTopSafeArea: Bool = false {
    didSet {
      props.ignoresTopSafeArea = ignoresTopSafeArea
    }
  }
  
  @objc var selectedPage: NSString? {
    didSet {
      props.selectedPage = selectedPage as? String
    }
  }
  
  @objc var scrollEdgeAppearance: NSString? {
    didSet {
      props.scrollEdgeAppearance = scrollEdgeAppearance as? String
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
  
#if !os(macOS)
  public override func layoutSubviews() {
    super.layoutSubviews()
    setupView()
    props.children = reactSubviews()
  }
#else
  public override func layout() {
    super.layout()
    setupView()
    props.children = reactSubviews()
  }
#endif
  
  private func setupView() {
    if self.hostingController != nil {
      return
    }
    
    let eventDispatchCallback: (_ key: String) -> Void = { key in
      self.coalescingKey += 1
      self.eventDispatcher?.send(PageSelectedEvent(reactTag: self.reactTag, key: NSString(string: key), coalescingKey: self.coalescingKey))
    }
    
#if !os(macOS)
    self.hostingController = UIHostingController(rootView: TabViewImpl(props: props, onSelect: eventDispatchCallback))
#else
    self.hostingController = NSHostingController(rootView: TabViewImpl(props: props, onSelect: eventDispatchCallback))
#endif
    if let hostingController = self.hostingController, let parentViewController = reactViewController() {
      parentViewController.addChild(hostingController)
      addSubview(hostingController.view)
      hostingController.view.translatesAutoresizingMaskIntoConstraints = false
      hostingController.view.pinEdges(to: self)
      
#if !os(macOS)
      hostingController.didMove(toParent: parentViewController)
#endif
    }
  }
  
  private func loadIcons(_ icons: NSArray?) {
    // TODO: Diff the arrays and update only changed items.
    // Now if the user passes `unfocusedIcon` we update every item.
    if let imageSources = icons as? [RCTImageSource?] {
      for (index, imageSource) in imageSources.enumerated() {
        guard let imageSource, let imageLoader else { continue }
        imageLoader.loadImage(
          with: imageSource.request,
          size: imageSource.size,
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
              self.props.icons[index] = image.resizeImageTo(size: self.iconSize)
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
            badge: itemDict["badge"] as? String ?? "",
            sfSymbol: itemDict["sfSymbol"] as? String ?? ""
          )
        )
      }
    }
    
    return TabData(tabs: items)
  }
}

extension PlatformImage {
  func resizeImageTo(size: CGSize) -> PlatformImage? {
#if os(iOS)
    UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
    self.draw(in: CGRect(origin: .zero, size: size))
    let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage
#elseif os(macOS)
    let newImage = NSImage(size: size)
    newImage.lockFocus()
    self.draw(in: CGRect(origin: .zero, size: size),
              from: CGRect(origin: .zero, size: self.size),
              operation: .copy,
              fraction: 1.0)
    newImage.unlockFocus()
    return newImage
#endif
  }
}
