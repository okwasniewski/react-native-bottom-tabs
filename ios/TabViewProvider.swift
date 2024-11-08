import Foundation
import SwiftUI
import React
import SDWebImage
import SDWebImageSVGCoder

@objc public final class TabInfo: NSObject {
  @objc public let key: String
  @objc public let title: String
  @objc public let badge: String
  @objc public let sfSymbol: String
  @objc public let activeTintColor: UIColor?
  @objc public let hidden: Bool
  
  @objc
  public init(
    key: String,
    title: String,
    badge: String,
    sfSymbol: String,
    activeTintColor: UIColor?,
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
}

@objc public class TabViewProvider: UIView {
  private var delegate: TabViewProviderDelegate?
  private var props = TabViewProps()
  private var hostingController: UIHostingController<TabViewImpl>?
  private var coalescingKey: UInt16 = 0
  private var imageLoader: RCTImageLoaderProtocol?
  private var iconSize = CGSize(width: 27, height: 27)

  @objc var onPageSelected: RCTDirectEventBlock?

  @objc var onTabLongPress: RCTDirectEventBlock?

  @objc public var icons: NSArray? {
    didSet {
      loadIcons(icons)
    }
  }

  @objc public var children: [UIView] = [] {
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

  @objc public var hapticFeedbackEnabled: Bool = true {
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

  @objc public var barTintColor: UIColor? {
    didSet {
      props.barTintColor = barTintColor
    }
  }

  @objc public var activeTintColor: UIColor? {
    didSet {
      props.activeTintColor = activeTintColor
    }
  }

  @objc public var inactiveTintColor: UIColor? {
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

  @objc public convenience init(delegate: TabViewProviderDelegate, imageLoader: RCTImageLoader) {
    self.init()
    self.delegate = delegate
    self.imageLoader = imageLoader
    SDImageCodersManager.shared.addCoder(SDImageSVGCoder.shared)
  }

  public override func didUpdateReactSubviews() {
    props.children = reactSubviews()
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    setupView()
  }

  private func setupView() {
    if self.hostingController != nil {
      return
    }

    self.hostingController = UIHostingController(rootView: TabViewImpl(props: props) { key in
      self.delegate?.onPageSelected(key: key, reactTag: self.reactTag)
    } onLongPress: { key in
      self.delegate?.onLongPress(key: key, reactTag: self.reactTag)
    })
    if let hostingController = self.hostingController, let parentViewController = reactViewController() {
      parentViewController.addChild(hostingController)
      addSubview(hostingController.view)
      hostingController.view.translatesAutoresizingMaskIntoConstraints = false
      hostingController.view.pinEdges(to: self)
      hostingController.didMove(toParent: parentViewController)
    }
  }

  private func loadIcons(_ icons: NSArray?) {
    // TODO: Diff the arrays and update only changed items.
    // Now if the user passes `unfocusedIcon` we update every item.
    guard let imageSources = icons as? [RCTImageSource?] else { return }


    for (index, imageSource) in imageSources.enumerated() {
      guard let imageSource = imageSource,
            let urlString = imageSource.request.url?.absoluteString else { continue }

      let url = URL(string: urlString)
      let isSVG = url?.pathExtension.lowercased() == "svg"

      // Configure SVG specific options if needed
      var options: SDWebImageOptions = [.continueInBackground]
      if isSVG {
        options.insert(.decodeFirstFrameOnly)
      }

      // Create context options for SVG rendering
      let context: [SDWebImageContextOption: Any]? = isSVG ? [
        .svgImageSize: iconSize,
        .imageThumbnailPixelSize: iconSize
      ] : nil

      SDWebImageManager.shared.loadImage(
        with: url,
        options: options,
        context: context,
        progress: nil
      ) { [weak self] (image, _, _, _, _, _) in
        guard let self = self else { return }

        DispatchQueue.main.async {
          if let image = image {
            if isSVG {
              // SVG images are already sized correctly through the context options
              self.props.icons[index] = image
            } else {
              // Resize non-SVG images
              if let resizedImage = image.sd_resizedImage(
                with: self.iconSize,
                scaleMode: .aspectFit
              ) {
                self.props.icons[index] = resizedImage
              }
            }
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
