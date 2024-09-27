import Foundation
import SwiftUI

/**
 Props that component accepts. SwiftUI view gets re-rendered when ObservableObject changes.
 */
class TabViewProps: ObservableObject {
  @Published var children: [UIView]?
  @Published var items: TabData?
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
  
  var body: some View {
    TabView {
      ForEach(props.children?.indices ?? 0..<0, id: \.self) { index in
        let child = props.children?[safe: index] ?? UIView()
        let tabData = props.items?.tabs[safe: index]
        RepresentableView(view: child)
          .frame(width: child.frame.width, height: child.frame.height)
          .tabItem {
            if #available(iOS 14.0, *) {
              Label(tabData?.title ?? "", systemImage: tabData?.icon ?? "")
            } else {
              Text(tabData?.title ?? "")
            }
          }
      }
    }.sidebarAdaptable()
  }
}

extension View {
    @ViewBuilder
    func sidebarAdaptable() -> some View {
        if #available(iOS 18.0, macOS 15.0, *) {
            self.tabViewStyle(.sidebarAdaptable)
        } else {
            self
        }
    }
}
