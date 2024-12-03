import Foundation
import SwiftUI
import React
import SDWebImage
import SDWebImageSVGCoder

@objcMembers
public final class TabInfo: NSObject {
  public let key: String
  public let title: String
  public let badge: String
  public let sfSymbol: String
  public let activeTintColor: PlatformColor?
  public let hidden: Bool

  public init(
    key: String,
    title: String,
    badge: String,
    sfSymbol: String,
    activeTintColor: PlatformColor?,
    hidden: Bool
  ) {
    self.key = key
    self.title = title
    self.badge = badge
    self.sfSymbol = sfSymbol
    self.activeTintColor = activeTintColor
    self.hidden = hidden
    super.init()
  }
}

@objc public protocol TabViewProviderDelegate {
  func onPageSelected(key: String, reactTag: NSNumber?)
  func onLongPress(key: String, reactTag: NSNumber?)
  func onTabBarMeasured(height: Int, reactTag: NSNumber?)
  func onLayout(size: CGSize, reactTag: NSNumber?)
}

@objc public class TabViewProvider: PlatformView {
  private var delegate: TabViewProviderDelegate?
  private var props = TabViewProps()
  private var hostingController: PlatformHostingController<TabViewImpl>?
  private var coalescingKey: UInt16 = 0
  private var iconSize = CGSize(width: 27, height: 27)

  @objc var onPageSelected: RCTDirectEventBlock?

  @objc var onTabLongPress: RCTDirectEventBlock?
  @objc var onTabBarMeasured: RCTDirectEventBlock?
  @objc var onNativeLayout: RCTDirectEventBlock?

  @objc public var icons: NSArray? {
    didSet {
      loadIcons(icons)
    }
  }

  @objc public var children: [PlatformView] = [] {
    didSet {
      props.children = children
    }
  }

  @objc public var sidebarAdaptable: Bool = false {
    didSet {
      props.sidebarAdaptable = sidebarAdaptable
    }
  }


  @objc public var disablePageAnimations: Bool = false {
    didSet {
      props.disablePageAnimations = disablePageAnimations
    }
  }

  @objc public var labeled: Bool = true {
    didSet {
      props.labeled = labeled
    }
  }

  @objc public var ignoresTopSafeArea: Bool = true {
    didSet {
      props.ignoresTopSafeArea = ignoresTopSafeArea
    }
  }

  @objc public var selectedPage: NSString? {
    didSet {
      props.selectedPage = selectedPage as? String
    }
  }

  @objc public var hapticFeedbackEnabled: Bool = false {
    didSet {
      props.hapticFeedbackEnabled = hapticFeedbackEnabled
    }
  }

  @objc public var scrollEdgeAppearance: NSString? {
    didSet {
      props.scrollEdgeAppearance = scrollEdgeAppearance as? String
    }
  }

  @objc public var translucent: Bool = true {
    didSet {
      props.translucent = translucent
    }
  }

  @objc var items: NSArray? {
    didSet {
      props.items = parseTabData(from: items)
    }
  }

  @objc public var barTintColor: PlatformColor? {
    didSet {
      props.barTintColor = barTintColor
    }
  }

  @objc public var activeTintColor: PlatformColor? {
    didSet {
      props.activeTintColor = activeTintColor
    }
  }

  @objc public var inactiveTintColor: PlatformColor? {
    didSet {
      props.inactiveTintColor = inactiveTintColor
    }
  }

  @objc public var fontFamily: NSString? {
    didSet {
      props.fontFamily = fontFamily as? String
    }
  }

  @objc public var fontWeigth: NSString? {
    didSet {
      props.fontWeight = fontWeigth as? String
    }
  }

  @objc public var fontSize: NSNumber? {
    didSet {
      props.fontSize = fontSize as? Int
    }
  }

  // New arch specific properties

  @objc public var itemsData: [TabInfo] = [] {
    didSet {
      props.items = itemsData
    }
  }

  @objc public convenience init(delegate: TabViewProviderDelegate) {
    self.init()
    self.delegate = delegate
    SDImageCodersManager.shared.addCoder(SDImageSVGCoder.shared)
  }

  public override func didUpdateReactSubviews() {
    props.children = reactSubviews()
  }

#if os(macOS)
  public override func layout() {
    super.layout()
    setupView()
  }
#else
  public override func layoutSubviews() {
    super.layoutSubviews()
    setupView()
  }
#endif

  private func setupView() {
    if self.hostingController != nil {
      return
    }

    self.hostingController = PlatformHostingController(rootView: TabViewImpl(props: props) { key in
      self.delegate?.onPageSelected(key: key, reactTag: self.reactTag)
    } onLongPress: { key in
      self.delegate?.onLongPress(key: key, reactTag: self.reactTag)
    } onLayout: { size  in
      self.delegate?.onLayout(size: size, reactTag: self.reactTag)
    } onTabBarMeasured: { height in
      self.delegate?.onTabBarMeasured(height: height, reactTag: self.reactTag)
    })

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
    guard let imageSources = icons as? [RCTImageSource?] else { return }

    for (index, imageSource) in imageSources.enumerated() {
      guard let imageSource = imageSource,
            let url = imageSource.request.url else { continue }

      let isSVG = url.pathExtension.lowercased() == "svg"

      var options: SDWebImageOptions = [.continueInBackground,
                                        .scaleDownLargeImages,
                                        .avoidDecodeImage,
                                        .highPriority]

      if isSVG {
        options.insert(.decodeFirstFrameOnly)
      }

      let context: [SDWebImageContextOption: Any]? = isSVG ? [
        .imageThumbnailPixelSize: iconSize,
        .imagePreserveAspectRatio: true
      ] : nil

      SDWebImageManager.shared.loadImage(
        with: url,
        options: options,
        context: context,
        progress: nil
      ) { [weak self] (image, _, _, _, _, _) in
        guard let self = self else { return }
        DispatchQueue.main.async {
          if let image {
            self.props.icons[index] = image.resizeImageTo(size: self.iconSize)
          }
        }
      }
    }
  }

  private func parseTabData(from array: NSArray?) -> [TabInfo] {
    guard let array else { return [] }
    var items: [TabInfo] = []

    for value in array {
      if let itemDict = value as? [String: Any] {
        items.append(
          TabInfo(
            key: itemDict["key"] as? String ?? "",
            title: itemDict["title"] as? String ?? "",
            badge: itemDict["badge"] as? String ?? "",
            sfSymbol: itemDict["sfSymbol"] as? String ?? "",
            activeTintColor: RCTConvert.uiColor(itemDict["activeTintColor"] as? NSNumber),
            hidden: itemDict["hidden"] as? Bool ?? false
          )
        )
      }
    }

    return items
  }
}
