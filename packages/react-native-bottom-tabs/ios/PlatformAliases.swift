import SwiftUI

#if os(macOS)
import AppKit
#else
import UIKit
#endif

#if os(macOS)
public typealias PlatformView = NSView
#else
public typealias PlatformView = UIView
#endif

#if os(macOS)
typealias PlatformImage = NSImage
#else
typealias PlatformImage = UIImage
#endif

#if os(macOS)
public typealias PlatformColor = NSColor
#else
public typealias PlatformColor = UIColor
#endif

#if os(macOS)
typealias PlatformHostingController = NSHostingController
#else
typealias PlatformHostingController = UIHostingController
#endif

#if os(macOS)
typealias PlatformViewRepresentable = NSViewRepresentable
#else
typealias PlatformViewRepresentable = UIViewRepresentable
#endif
