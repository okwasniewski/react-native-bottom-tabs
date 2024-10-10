import UIKit

class TabViewProps {
    var children: [UIView]?
    var config: TabViewConfig?
    var items: TabData?
    var selectedPage: String?
    var icons: [Int: UIImage] = [:]
}

class TabViewImpl: UITabBarController {
    var props: TabViewProps
    var onSelect: (_ key: String) -> Void

    init(props: TabViewProps, onSelect: @escaping (_ key: String) -> Void) {
        self.props = props
        self.onSelect = onSelect
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTabs()
    }

    func setupTabs() {
        guard let children = props.children, let items = props.items?.tabs else { return }

        var viewControllers: [UIViewController] = []
        
        for (index, childView) in children.enumerated() {
            let childViewController = UIViewController()
            childViewController.view = childView
            let tabData = items[safe: index]
            
            // Configure the tab bar item
            let tabBarItem = UITabBarItem(title: tabData?.title, image: props.icons[index], tag: index)
            if let badge = tabData?.badge, !badge.isEmpty {
                tabBarItem.badgeValue = badge
            }
            childViewController.tabBarItem = tabBarItem
            
            viewControllers.append(childViewController)
        }
        
        self.viewControllers = viewControllers
        // Optionally select the initial tab
        if let selectedPage = props.selectedPage, let initialIndex = items.firstIndex(where: { $0.key == selectedPage }) {
            self.selectedIndex = initialIndex
        }
        
        self.delegate = self
    }
}

// Implementing UITabBarControllerDelegate to handle tab selection
extension TabViewImpl: UITabBarControllerDelegate {
    func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {
        if let index = tabBarController.viewControllers?.firstIndex(of: viewController),
           let tabData = props.items?.tabs[safe: index] {
            onSelect(tabData.key)
        }
    }
}
