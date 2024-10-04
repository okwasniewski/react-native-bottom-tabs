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
  @Published var tabViewStyle: String?
  @Published var icons: [Int: UIImage] = [:]
}

/**
 Helper used to render UIView inside of SwiftUI.
 */
struct RepresentableView: UIViewRepresentable {
  var view: UIView
  func makeUIView(context: Context) -> UIView {
    return view
  }
  func updateUIView(_ uiView: UIView, context: Context) {
  }
}

/**
 SwiftUI implementation of TabView used to render React Native views.
 */
struct TabViewImpl: View {
  @ObservedObject var props: TabViewProps
  var onSelect: (_ key: String) -> Void
  
  var body: some View {
    TabView(selection: $props.selectedPage) {
      ForEach(props.children?.indices ?? 0..<0, id: \.self) { index in
        let child = props.children?[safe: index] ?? UIView()
        let tabData = props.items?.tabs[safe: index]
        let icon = props.icons[index]
        RepresentableView(view: child)
          .frame(maxWidth: .infinity, maxHeight: .infinity)
          .tabItem {
            TabItem(icon: icon, sfSymbol: tabData?.sfSymbol, title: tabData?.title)
          }
          .tag(tabData?.key)
          .tabBadge(tabData?.badge)
      }
      .getTabViewStyle(name: props.tabViewStyle ?? "")
      .onChange(of: props.selectedPage ?? "") { newValue in
        onSelect(newValue)
      }
    }
  }
}

struct TabItem: View {
  var icon: UIImage?
  var sfSymbol: String?
  var title: String?
  
  var body: some View {
    if let icon {
      Image(uiImage: icon)
    } else if let sfSymbol, !sfSymbol.isEmpty {
      Image(systemName: sfSymbol)
    }
    Text(title ?? "")
  }
}

extension View {
  @ViewBuilder
  func getTabViewStyle(name: String) -> some View {
    switch name {
    case "automatic":
      self.tabViewStyle(.automatic)
    case "sidebarAdaptable":
      if #available(iOS 18.0, macOS 15.0, tvOS 18.0, visionOS 2.0, *) {
        self.tabViewStyle(.sidebarAdaptable)
      } else {
        self
      }
    default:
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
}
