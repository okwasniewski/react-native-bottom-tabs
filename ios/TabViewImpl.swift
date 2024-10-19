import Foundation
import SwiftUI
import React

#if os(iOS)
import UIKit
#elseif os(macOS)
import AppKit
#endif

/**
 Props that component accepts. SwiftUI view gets re-rendered when ObservableObject changes.
 */
class TabViewProps: ObservableObject {
  @Published var children: [PlatformView]?
  @Published var items: TabData?
  @Published var selectedPage: String?
  @Published var icons: [Int: PlatformImage] = [:]
  @Published var sidebarAdaptable: Bool?
  @Published var labeled: Bool?
  @Published var ignoresTopSafeArea: Bool?
  @Published var disablePageAnimations: Bool = false
  @Published var scrollEdgeAppearance: String?
}

#if !os(macOS)
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

#else
struct RepresentableView: NSViewRepresentable {
  var view: NSView
  
  func makeNSView(context: Context) -> NSView {
    return view
  }
  
  func updateNSView(_ nsView: NSView, context: Context) {}
}
#endif

/**
 SwiftUI implementation of TabView used to render React Native views.
 */
struct TabViewImpl: View {
  @ObservedObject var props: TabViewProps
  var onSelect: (_ key: String) -> Void
  
  var body: some View {
    TabView(selection: $props.selectedPage) {
      ForEach(props.children?.indices ?? 0..<0, id: \.self) { index in
        let child = props.children?[safe: index] ?? PlatformView()
        let tabData = props.items?.tabs[safe: index]
        let icon = props.icons[index]
        
        RepresentableView(view: child)
          .ignoresTopSafeArea(
            props.ignoresTopSafeArea ?? false
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
      onSelect(newValue)
    }
#if !os(macOS)
    .onChange(of: props.scrollEdgeAppearance) { newValue in
      if #available(iOS 15.0, *) {
        UITabBar.appearance().scrollEdgeAppearance = configureAppearance(for: newValue ?? "")
      }
    }
#endif
  }
}

#if !os(macOS)
private func configureAppearance(for appearanceType: String) -> UITabBarAppearance {
  let appearance = UITabBarAppearance()
  
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
#endif

struct TabItem: View {
  var title: String?
  var icon: PlatformImage?
  var sfSymbol: String?
  var labeled: Bool?
  
  var body: some View {
    if let icon {
#if os(iOS)
      Image(uiImage: icon)
#elseif os(macOS)
      Image(nsImage: icon)
#endif
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
  func ignoresTopSafeArea(_ flag: Bool) -> some View {
    if flag {
      self
        .ignoresSafeArea(.container, edges: .all)
    } else {
      self
        .ignoresSafeArea(.container, edges: [.horizontal, .bottom])
    }
  }

}
