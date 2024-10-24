import SwiftUI
import SwiftUIIntrospect

struct TabItemLongPressModifier: ViewModifier {
  let onLongPress: (Int) -> Void
  
  func body(content: Content) -> some View {
    content.introspect(.tabView, on: .iOS(.v13, .v14, .v15, .v16, .v17, .v18)) { tabController in
      // Remove existing long press gestures
      if let existingGestures = tabController.tabBar.gestureRecognizers {
        for gesture in existingGestures where gesture is UILongPressGestureRecognizer {
          tabController.tabBar.removeGestureRecognizer(gesture)
        }
      }
      
      // Create gesture handler
      let handler = LongPressGestureHandler(tabBar: tabController.tabBar, handler: onLongPress)
      let gesture = UILongPressGestureRecognizer(target: handler, action: #selector(LongPressGestureHandler.handleLongPress(_:)))
      gesture.minimumPressDuration = 0.5
      
      objc_setAssociatedObject(tabController.tabBar, &AssociatedKeys.gestureHandler, handler, .OBJC_ASSOCIATION_RETAIN)
      
      tabController.tabBar.addGestureRecognizer(gesture)
    }
  }
}

private struct AssociatedKeys {
  static var gestureHandler: UInt8 = 0
}

private class LongPressGestureHandler: NSObject {
  private weak var tabBar: UITabBar?
  private let handler: (Int) -> Void
  
  init(tabBar: UITabBar, handler: @escaping (Int) -> Void) {
    self.tabBar = tabBar
    self.handler = handler
    super.init()
  }
  
  @objc func handleLongPress(_ recognizer: UILongPressGestureRecognizer) {
    guard recognizer.state == .began,
          let tabBar = tabBar else { return }
    
    let location = recognizer.location(in: tabBar)

    // Get buttons and sort them by frames
    let tabBarButtons = tabBar.subviews.filter { String(describing: type(of: $0)).contains("UITabBarButton") }.sorted(by: { $0.frame.minX < $1.frame.minX })
    
    for (index, button) in tabBarButtons.enumerated() {
      if button.frame.contains(location) {
        handler(index)
        break
      }
    }
  }
}

extension View {
  func onTabItemLongPress(_ handler: @escaping (Int) -> Void) -> some View {
    modifier(TabItemLongPressModifier(onLongPress: handler))
  }
}
