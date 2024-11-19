import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ColorValue, ProcessedColorValue, ViewProps } from 'react-native';
import type {
  DirectEventHandler,
  Int32,
  Double,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';
//@ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';

export type OnPageSelectedEventData = Readonly<{
  key: string;
}>;

export type OnTabViewLayoutEventdata = Readonly<{
  width: Double;
  height: Double;
}>;

export type TabViewItems = ReadonlyArray<{
  key: string;
  title: string;
  sfSymbol?: string;
  badge?: string;
  activeTintColor?: ProcessedColorValue | null;
  hidden?: boolean;
}>;

export interface TabViewProps extends ViewProps {
  items: TabViewItems;
  selectedPage: string;
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  onTabLongPress?: DirectEventHandler<OnPageSelectedEventData>;
  onTabViewLayout?: DirectEventHandler<OnTabViewLayoutEventdata>;
  icons?: ReadonlyArray<ImageSource>;
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  scrollEdgeAppearance?: string;
  barTintColor?: ColorValue;
  translucent?: boolean;
  rippleColor?: ColorValue;
  activeTintColor?: ColorValue;
  inactiveTintColor?: ColorValue;
  ignoresTopSafeArea?: WithDefault<boolean, true>;
  disablePageAnimations?: boolean;
  activeIndicatorColor?: ColorValue;
  hapticFeedbackEnabled?: boolean;
  fontFamily?: string;
  fontWeight?: string;
  fontSize?: Int32;
}

export default codegenNativeComponent<TabViewProps>('RNCTabView', {
  interfaceOnly: true,
});
