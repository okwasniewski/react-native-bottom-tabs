import SwiftUI
import SwiftUIIntrospect

private final class TabBarDelegate: NSObject, UITabBarControllerDelegate {
  var onClick: ((_ index: Int) -> Void)? = nil
  
  func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
    if let index = tabBarController.viewControllers?.firstIndex(of: viewController) {
      onClick?(index)
    }
    return false
  }
}

struct TabItemEventModifier: ViewModifier {
  let onTabEvent: (_ key: Int, _ isLongPress: Bool) -> Void
  private let delegate = TabBarDelegate()
  
  func body(content: Content) -> some View {
    content
      .introspect(.tabView, on: .iOS(.v14, .v15, .v16, .v17, .v18)) { tabController in
        handle(tabController: tabController)
      }
      .introspect(.tabView, on: .tvOS(.v14, .v15, .v16, .v17, .v18)) { tabController in
        handle(tabController: tabController)
      }
  }
  
  func handle(tabController: UITabBarController) {
    delegate.onClick = { index in
      onTabEvent(index, false)
    }
    tabController.delegate = delegate
    
    // Don't register gesutre recognizer more than one time
    if objc_getAssociatedObject(tabController.tabBar, &AssociatedKeys.gestureHandler) != nil {
      return
    }
    
    // Remove existing long press gestures
    if let existingGestures = tabController.tabBar.gestureRecognizers {
      for gesture in existingGestures where gesture is UILongPressGestureRecognizer {
        tabController.tabBar.removeGestureRecognizer(gesture)
      }
    }
    
    // Create gesture handler
    let handler = LongPressGestureHandler(tabBar: tabController.tabBar, handler: onTabEvent)
    let gesture = UILongPressGestureRecognizer(target: handler, action: #selector(LongPressGestureHandler.handleLongPress(_:)))
    gesture.minimumPressDuration = 0.5
    
    objc_setAssociatedObject(tabController.tabBar, &AssociatedKeys.gestureHandler, handler, .OBJC_ASSOCIATION_RETAIN)
    
    tabController.tabBar.addGestureRecognizer(gesture)
  }
}

private struct AssociatedKeys {
  static var gestureHandler: UInt8 = 0
}

private class LongPressGestureHandler: NSObject {
  private weak var tabBar: UITabBar?
  private let handler: (Int, Bool) -> Void
  
  init(tabBar: UITabBar, handler: @escaping (Int, Bool) -> Void) {
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
        handler(index, true)
        break
      }
    }
  }
  
  deinit {
    if let tabBar {
      objc_setAssociatedObject(tabBar, &AssociatedKeys.gestureHandler, nil, .OBJC_ASSOCIATION_RETAIN)
    }
  }
}

extension View {
  func onTabItemEvent(_ handler: @escaping (Int, Bool) -> Void) -> some View {
    modifier(TabItemEventModifier(onTabEvent: handler))
  }
}
