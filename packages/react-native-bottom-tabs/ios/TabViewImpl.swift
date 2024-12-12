import Foundation
import SwiftUI
import React
@_spi(Advanced) import SwiftUIIntrospect


/**
 SwiftUI implementation of TabView used to render React Native views.
 */
struct TabViewImpl: View {
  @ObservedObject var props: TabViewProps
#if os(macOS)
  @Weak var tabBar: NSTabView?
#else
  @Weak var tabBar: UITabBar?
#endif

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
#if !os(tvOS) && !os(macOS)
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
#if os(macOS)
      tabBar = tabController
#else
      tabBar = tabController.tabBar
      onTabBarMeasured(
        Int(tabController.tabBar.frame.size.height)
      )
#endif
    })
#if !os(macOS)
    .configureAppearance(props: props, tabBar: tabBar)
#endif
    .tintColor(props.selectedActiveTintColor)
    .getSidebarAdaptable(enabled: props.sidebarAdaptable ?? false)
    .onChange(of: props.selectedPage ?? "") { newValue in
#if !os(macOS)
      if (props.disablePageAnimations) {
        UIView.setAnimationsEnabled(false)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
          UIView.setAnimationsEnabled(true)
        }
      }
#endif
#if os(tvOS) || os(macOS)
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
      let child = props.children[safe: index] ?? PlatformView()
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
          .accessibilityIdentifier(tabData?.testID ?? "")
        }
        .tag(tabData?.key)
        .tabBadge(tabData?.badge)
        .onAppear {
#if !os(macOS)
          updateTabBarAppearance(props: props, tabBar: tabBar)
#endif

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

#if !os(macOS)
private func updateTabBarAppearance(props: TabViewProps, tabBar: UITabBar?) {
  guard let tabBar else { return }

  if props.scrollEdgeAppearance == "transparent" {
    configureTransparentAppearance(tabBar: tabBar, props: props)
    return
  }

  configureStandardAppearance(tabBar: tabBar, props: props)
}
#endif

private func createFontAttributes(
  size: CGFloat,
  family: String?,
  weight: String?,
  inactiveTintColor: PlatformColor?
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


#if !os(macOS)
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
  
  if props.translucent == false {
    appearance.configureWithOpaqueBackground()
  }
  
  if props.barTintColor != nil {
    appearance.backgroundColor = props.barTintColor
  }
  
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

  // Apply final appearance
  tabBar.standardAppearance = appearance
  if #available(iOS 15.0, *) {
    tabBar.scrollEdgeAppearance = appearance.copy()
  }
}
#endif

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

#if !os(macOS)
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
  }
#endif

  @ViewBuilder
  func tintColor(_ color: PlatformColor?) -> some View {
    if let color {
      let color = Color(color)
      if #available(iOS 16.0, tvOS 16.0, macOS 13.0, *) {
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
    if #available(iOS 15.0, tvOS 15.0, macOS 13.0, *) {
      self
        .environment(\.symbolVariants, .none)
    } else {
      self
    }
  }
}
