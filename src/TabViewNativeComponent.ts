import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ColorValue, ProcessedColorValue, ViewProps } from 'react-native';
import type {
  DirectEventHandler,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';
//@ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';

export type OnPageSelectedEventData = Readonly<{
  key: string;
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
}

export default codegenNativeComponent<TabViewProps>('RNCTabView', {
  interfaceOnly: true,
});
