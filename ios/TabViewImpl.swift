import Foundation
import SwiftUI
import React

/**
 Props that component accepts. SwiftUI view gets re-rendered when ObservableObject changes.
 */
class TabViewProps: ObservableObject {
  @Published var children: [UIView]?
  @Published var items: TabData?
  @Published var selectedPage: String?
  @Published var icons: [Int: UIImage] = [:]
  @Published var sidebarAdaptable: Bool?
  @Published var labeled: Bool?
  @Published var ignoresTopSafeArea: Bool?
  @Published var disablePageAnimations: Bool = false
  @Published var scrollEdgeAppearance: String?
  @Published var barTintColor: UIColor?
  @Published var translucent: Bool = true
  @Published var activeTintColor: UIColor?
  @Published var inactiveTintColor: UIColor?
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
  var onSelect: (_ key: String) -> Void

  var selectedActiveTintColor: UIColor? {
    // check first in selected tab
    let selectedPage = props.selectedPage
    if let selectedPage {
      if let tabData = props.items?.tabs.findByKey(selectedPage) {
        if let activeTintColor = tabData.activeTintColor {
          return RCTConvert.uiColor(activeTintColor)
        }
      }
    }

    if let activeTintColor = props.activeTintColor {
      return activeTintColor
    }

    return nil
  }

  var body: some View {
    TabView(selection: $props.selectedPage) {
      ForEach(props.children?.indices ?? 0..<0, id: \.self) { index in
        let child = props.children?[safe: index] ?? UIView()
        let tabData = props.items?.tabs[safe: index]
        let icon = props.icons[index]

        RepresentableView(view: child)
          .ignoresTopSafeArea(
            props.ignoresTopSafeArea ?? false,
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
      }
    }
    .tintColor(selectedActiveTintColor)
    .getSidebarAdaptable(enabled: props.sidebarAdaptable ?? false)
    .onChange(of: props.selectedPage ?? "") { newValue in
      if (props.disablePageAnimations) {
        UIView.setAnimationsEnabled(false)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
          UIView.setAnimationsEnabled(true)
        }
      }

      // to apply active tint color per tab
      let scrollEdgeAppearance = configureAppearance(for: props.scrollEdgeAppearance ?? "")
      let colorTintAppearance = configureAppearance(appearance: scrollEdgeAppearance, inactiveTint: props.inactiveTintColor, activeTint: selectedActiveTintColor)
      setupTabBarAppearance(colorTintAppearance)

      onSelect(newValue)
    }
    .onAppear() {
      updateTabBarAppearance(props: props)
    }
    .onChange(of: props.barTintColor) { newValue in
      updateTabBarAppearance(props: props)
    }
    .onChange(of: props.scrollEdgeAppearance) { newValue in
      updateTabBarAppearance(props: props)
    }
    .onChange(of: props.translucent) { newValue in
      updateTabBarAppearance(props: props)
    }
    .onAppear {
      // we have to keep onAppear to setup the appearance for the first render.
      let scrollEdgeAppearance = configureAppearance(for: props.scrollEdgeAppearance ?? "")
      let colorTintAppearance = configureAppearance(appearance: scrollEdgeAppearance, inactiveTint: props.inactiveTintColor, activeTint: selectedActiveTintColor)
      setupTabBarAppearance(colorTintAppearance)
    }
  }
}

private func configureAppearance(for appearanceType: String, appearance: UITabBarAppearance) -> UITabBarAppearance {

  switch appearanceType {
  case "opaque":
    appearance.configureWithOpaqueBackground()
  case "transparent":
    appearance.configureWithTransparentBackground()
  default:
    appearance.configureWithDefaultBackground()
  }

  return appearance
}

private func configureAppearance(appearance: UITabBarAppearance, inactiveTint inactiveTintColor: UIColor?, activeTint activeTintColor: UIColor?) -> UITabBarAppearance {
  // @see https://stackoverflow.com/a/71934882
  if let inactiveTintColor {
    setTabBarItemColors(appearance.stackedLayoutAppearance, inactiveColor: inactiveTintColor)
    setTabBarItemColors(appearance.inlineLayoutAppearance, inactiveColor: inactiveTintColor)
    setTabBarItemColors(appearance.compactInlineLayoutAppearance, inactiveColor: inactiveTintColor)
  }

  if let activeTintColor {
    setTabBarItemColors(appearance.stackedLayoutAppearance, activeColor: activeTintColor)
    setTabBarItemColors(appearance.inlineLayoutAppearance, activeColor: activeTintColor)
    setTabBarItemColors(appearance.compactInlineLayoutAppearance, activeColor: activeTintColor)
  }

  return appearance
}

private func updateTabBarAppearance(props: TabViewProps) {
  if #available(iOS 15.0, *) {
    let appearance = UITabBarAppearance()

    UITabBar.appearance().scrollEdgeAppearance = configureAppearance(for: props.scrollEdgeAppearance ?? "", appearance: appearance)

    if props.translucent == false {
      appearance.configureWithOpaqueBackground()
    }

    if props.barTintColor != nil {
      appearance.backgroundColor = props.barTintColor
    }

    UITabBar.appearance().standardAppearance = appearance
  } else {
    UITabBar.appearance().barTintColor = props.barTintColor
    UITabBar.appearance().isTranslucent = props.translucent
  }
}

private func setupTabBarAppearance(_ appearance: UITabBarAppearance) {
      if #available(iOS 15.0, *) {
      UITabBar.appearance().scrollEdgeAppearance = appearance
    }
    UITabBar.appearance().standardAppearance = appearance
}

private func setTabBarItemColors(_ itemAppearance: UITabBarItemAppearance, inactiveColor: UIColor) {
  itemAppearance.normal.iconColor = inactiveColor
  itemAppearance.normal.titleTextAttributes = [.foregroundColor: inactiveColor]
}


private func setTabBarItemColors(_ itemAppearance: UITabBarItemAppearance, activeColor: UIColor) {
  itemAppearance.selected.iconColor = activeColor
  itemAppearance.selected.titleTextAttributes = [.foregroundColor: activeColor]
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
    }
    if (labeled != false) {
      Text(title ?? "")
    }
  }
}

extension View {
  @ViewBuilder
  func getSidebarAdaptable(enabled: Bool) -> some View {
    if #available(iOS 18.0, macOS 15.0, tvOS 18.0, visionOS 2.0, *) {
      if (enabled) {
#if compiler(>=6.0)
        self.tabViewStyle(.sidebarAdaptable)
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
    if #available(iOS 15.0, macOS 15.0, visionOS 2.0, *) {
      if let data = data, !data.isEmpty {
        self.badge(data)
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
        .ignoresSafeArea(.container, edges: .all)
        .frame(idealWidth: frame.width, idealHeight: frame.height)
    } else {
      self
        .ignoresSafeArea(.container, edges: .horizontal)
        .ignoresSafeArea(.container, edges: .bottom)
        .frame(idealWidth: frame.width, idealHeight: frame.height)
      }
    }
  }

  @ViewBuilder
  func tintColor(_ color: UIColor?) -> some View {
    if let color {
      let color = Color(color)
      if #available(iOS 16.0, *) {
        self.tint(color)
      } else {
        self.accentColor(color)
      }
    } else {
      self
    }
  }
}
