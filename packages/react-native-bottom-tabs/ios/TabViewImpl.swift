import Foundation
import SwiftUI
import React
@_spi(Advanced) import SwiftUIIntrospect


/**
 Props that component accepts. SwiftUI view gets re-rendered when ObservableObject changes.
 */
class TabViewProps: ObservableObject {
  @Published var children: [UIView] = []
  @Published var items: [TabInfo] = []
  @Published var selectedPage: String?
  @Published var icons: [Int: UIImage] = [:]
  @Published var sidebarAdaptable: Bool?
  @Published var labeled: Bool?
  @Published var scrollEdgeAppearance: String?
  @Published var barTintColor: UIColor?
  @Published var activeTintColor: UIColor?
  @Published var inactiveTintColor: UIColor?
  @Published var translucent: Bool = true
  @Published var ignoresTopSafeArea: Bool = true
  @Published var disablePageAnimations: Bool = false
  @Published var hapticFeedbackEnabled: Bool = false
  @Published var borderColor: UIColor?
  @Published var fontSize: Int?
  @Published var fontFamily: String?
  @Published var fontWeight: String?

  var selectedActiveTintColor: UIColor? {
    if let selectedPage = selectedPage,
       let tabData = items.findByKey(selectedPage),
       let activeTintColor = tabData.activeTintColor {
      return activeTintColor
    }

    return activeTintColor
  }
}

/**
 Helper used to render UIView inside of SwiftUI.
 */
struct RepresentableView: UIViewRepresentable {
  var view: UIView

  func makeUIView(context: Context) -> UIView {
    return view
  }

  func updateUIView(_ uiView: UIView, context: Context) {}
}

/**
 SwiftUI implementation of TabView used to render React Native views.
 */
struct TabViewImpl: View {
  @ObservedObject var props: TabViewProps
  @Weak var tabBar: UITabBar?

  var onSelect: (_ key: String) -> Void
  var onLongPress: (_ key: String) -> Void
  var onLayout: (_ size: CGSize) -> Void
  var onTabBarMeasured: (_ height: Int) -> Void

  var body: some View {
    TabView(selection: $props.selectedPage) {
      ForEach(props.children.indices, id: \.self) { index in
        renderTabItem(at: index)
      }
      .measureView(onLayout: { size in
        onLayout(size)
      })
    }
#if !os(tvOS)
    .onTabItemEvent({ index, isLongPress in
      guard let key = props.items.filter({
        !$0.hidden || $0.key == props.selectedPage
      })[safe: index]?.key else { return }

      if isLongPress {
        onLongPress(key)
        emitHapticFeedback(longPress: true)
      } else {
        onSelect(key)
        emitHapticFeedback()
      }
    })
#endif
    .introspectTabView(closure: { tabController in
      tabBar = tabController.tabBar
      onTabBarMeasured(
        Int(tabController.tabBar.frame.size.height)
      )
    })
    .configureAppearance(props: props, tabBar: tabBar)
    .tintColor(props.selectedActiveTintColor)
    .getSidebarAdaptable(enabled: props.sidebarAdaptable ?? false)
    .onChange(of: props.selectedPage ?? "") { newValue in
      if (props.disablePageAnimations) {
        UIView.setAnimationsEnabled(false)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
          UIView.setAnimationsEnabled(true)
        }
      }
#if os(tvOS)
      onSelect(newValue)
#endif
    }
  }

  @ViewBuilder
  private func renderTabItem(at index: Int) -> some View {
    let tabData = props.items[safe: index]
    let isHidden = tabData?.hidden ?? false
    let isFocused = props.selectedPage == tabData?.key

    if !isHidden || isFocused {
      let child = props.children[safe: index] ?? UIView()
      let icon = props.icons[index]

      RepresentableView(view: child)
        .ignoresTopSafeArea(
          props.ignoresTopSafeArea,
          frame: child.frame
        )
        .tabItem {
          TabItem(
            title: tabData?.title,
            icon: icon,
            sfSymbol: tabData?.sfSymbol,
            labeled: props.labeled
          )
        }
        .tag(tabData?.key)
        .tabBadge(tabData?.badge)
        .onAppear {
          updateTabBarAppearance(props: props, tabBar: tabBar)

#if os(iOS)
          guard index >= 4,
                let key = tabData?.key,
                props.selectedPage != key else { return }
          onSelect(key)
#endif
        }
    }
  }

  func emitHapticFeedback(longPress: Bool = false) {
#if os(iOS)
    if !props.hapticFeedbackEnabled {
      return
    }

    if longPress {
      UINotificationFeedbackGenerator().notificationOccurred(.success)
    } else {
      UISelectionFeedbackGenerator().selectionChanged()
    }
#endif
  }
}

struct TabItem: View {
  var title: String?
  var icon: UIImage?
  var sfSymbol: String?
  var labeled: Bool?

  var body: some View {
    if let icon {
      Image(uiImage: icon)
    } else if let sfSymbol, !sfSymbol.isEmpty {
      Image(systemName: sfSymbol)
        .noneSymbolVariant()
    }
    if (labeled != false) {
      Text(title ?? "")
    }
  }
}

private func updateTabBarAppearance(props: TabViewProps, tabBar: UITabBar?) {
    guard let tabBar else { return }

    if props.scrollEdgeAppearance == "transparent" {
        configureTransparentAppearance(tabBar: tabBar, props: props)
        return
    }

    configureStandardAppearance(tabBar: tabBar, props: props)
}

private func createFontAttributes(
  size: CGFloat,
  family: String?,
  weight: String?,
  inactiveTintColor: UIColor?
) -> [NSAttributedString.Key: Any] {
  var attributes: [NSAttributedString.Key: Any] = [:]


  if family != nil || weight != nil {
    attributes[.font] = RCTFont.update(
      nil,
      withFamily: family,
      size: NSNumber(value: size),
      weight: weight,
      style: nil,
      variant: nil,
      scaleMultiplier: 1.0
    )
  } else {
    attributes[.font] = UIFont.boldSystemFont(ofSize: size)
  }

  return attributes
}

#if os(tvOS)
let tabBarDefaultFontSize: CGFloat = 30.0
#else
let tabBarDefaultFontSize: CGFloat = UIFont.smallSystemFontSize
#endif

private func configureTransparentAppearance(tabBar: UITabBar, props: TabViewProps) {
  tabBar.barTintColor = props.barTintColor
  tabBar.isTranslucent = props.translucent
  tabBar.unselectedItemTintColor = props.inactiveTintColor

  guard let items = tabBar.items else { return }

  let fontSize = props.fontSize != nil ? CGFloat(props.fontSize!) : tabBarDefaultFontSize
  let attributes = createFontAttributes(
    size: fontSize,
    family: props.fontFamily,
    weight: props.fontWeight,
    inactiveTintColor: nil
  )

  items.forEach { item in
    item.setTitleTextAttributes(attributes, for: .normal)
  }

  if let borderColor = props.borderColor {
    tabBar.layer.borderWidth = 0.5
    tabBar.layer.borderColor = borderColor.cgColor
  }
}

private func configureStandardAppearance(tabBar: UITabBar, props: TabViewProps) {
  let appearance = UITabBarAppearance()

  // Configure background
  switch props.scrollEdgeAppearance {
  case "opaque":
    appearance.configureWithOpaqueBackground()
  default:
    appearance.configureWithDefaultBackground()
  }
  appearance.backgroundColor = props.barTintColor

  // Configure item appearance
  let itemAppearance = UITabBarItemAppearance()
  let fontSize = props.fontSize != nil ? CGFloat(props.fontSize!) : tabBarDefaultFontSize

  var attributes = createFontAttributes(
    size: fontSize,
    family: props.fontFamily,
    weight: props.fontWeight,
    inactiveTintColor: props.inactiveTintColor
  )
  
  if let inactiveTintColor = props.inactiveTintColor {
    attributes[.foregroundColor] = inactiveTintColor
  }

  if let inactiveTintColor = props.inactiveTintColor {
    itemAppearance.normal.iconColor = inactiveTintColor
  }

  itemAppearance.normal.titleTextAttributes = attributes

  // Apply item appearance to all layouts
  appearance.stackedLayoutAppearance = itemAppearance
  appearance.inlineLayoutAppearance = itemAppearance
  appearance.compactInlineLayoutAppearance = itemAppearance

  if let borderColor = props.borderColor {
    appearance.shadowColor = borderColor
  }

  // Apply final appearance
  tabBar.standardAppearance = appearance
  if #available(iOS 15.0, *) {
    tabBar.scrollEdgeAppearance = appearance.copy()
  }
}

extension View {
  @ViewBuilder
  func getSidebarAdaptable(enabled: Bool) -> some View {
    if #available(iOS 18.0, macOS 15.0, tvOS 18.0, visionOS 2.0, *) {
      if (enabled) {
#if compiler(>=6.0)
        self.tabViewStyle(.sidebarAdaptable)
#else
        self
#endif
      } else {
        self
      }
    } else {
      self
    }
  }

  @ViewBuilder
  func tabBadge(_ data: String?) -> some View {
    if #available(iOS 15.0, macOS 15.0, visionOS 2.0, tvOS 15.0, *) {
      if let data = data, !data.isEmpty {
#if !os(tvOS)
        self.badge(data)
#else
        self
#endif
      } else {
        self
      }
    } else {
      self
    }
  }

  @ViewBuilder
  func ignoresTopSafeArea(
    _ flag: Bool,
    frame: CGRect
  ) -> some View {
    if flag {
      self
        .ignoresSafeArea(.container, edges: .vertical)
    } else {
      self
        .ignoresSafeArea(.container, edges: .bottom)
    }
  }

  @ViewBuilder
  func configureAppearance(props: TabViewProps, tabBar: UITabBar?) -> some View {
    self
      .onChange(of: props.barTintColor) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.scrollEdgeAppearance) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.translucent) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.inactiveTintColor) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.selectedActiveTintColor) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.fontSize) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.fontFamily) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.fontWeight) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
      .onChange(of: props.borderColor) { newValue in
        updateTabBarAppearance(props: props, tabBar: tabBar)
      }
  }

  @ViewBuilder
  func tintColor(_ color: UIColor?) -> some View {
    if let color {
      let color = Color(color)
      if #available(iOS 16.0, tvOS 16.0, *) {
        self.tint(color)
      } else {
        self.accentColor(color)
      }
    } else {
      self
    }
  }

  // Allows TabView to use unfilled SFSymbols.
  // By default they are always filled.
  @ViewBuilder
  func noneSymbolVariant() -> some View {
    if #available(iOS 15.0, tvOS 15.0, *) {
      self
        .environment(\.symbolVariants, .none)
    } else {
      self
    }
  }
}
