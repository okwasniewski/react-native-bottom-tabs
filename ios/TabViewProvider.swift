import Foundation
import SwiftUI

struct TabInfo: Codable {
    let key: String
    let icon: String
    let title: String
}

struct TabData: Codable {
    let tabs: [TabInfo]
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let tabDictionary = try container.decode([String: [String: String]].self)
        
        self.tabs = tabDictionary.map { (key, value) in
            TabInfo(key: key, icon: value["icon"] ?? "", title: value["title"] ?? "")
        }
    }
}

@objc public class TabViewProvider: UIView {
  var props = TabViewProps()
  private var hostingController: UIHostingController<TabViewImpl>?
  
  @objc var items: NSDictionary? {
    didSet {
      props.items = parseTabData(from: items)
    }
  }
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    setupView()
  }
  
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    setupView()
  }
  
  public override func didUpdateReactSubviews() {
    props.children = reactSubviews()
  }
  
  public override func layoutSubviews() {
    super.layoutSubviews()
    props.children = reactSubviews()
  }
  
  private func setupView() {
    self.hostingController = UIHostingController(rootView: TabViewImpl(props: props))
    if let hostingController = self.hostingController {
      addSubview(hostingController.view)
      hostingController.view.translatesAutoresizingMaskIntoConstraints = false
      hostingController.view.pinEdges(to: self)
    }
  }
  
  func parseTabData(from dictionary: NSDictionary?) -> TabData? {
      guard let dict = dictionary as? [String: Any] else {
          print("Failed to cast dictionary")
          return nil
      }
      
      do {
          let jsonData = try JSONSerialization.data(withJSONObject: dict, options: [])
          let decoder = JSONDecoder()
          let tabData = try decoder.decode(TabData.self, from: jsonData)
          return tabData
      } catch {
          print("Error decoding dictionary: \(error)")
          return nil
      }
  }
}
