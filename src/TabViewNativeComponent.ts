import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ColorValue, ProcessedColorValue, ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
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
}>;

export interface TabViewProps extends ViewProps {
  items: TabViewItems;
  selectedPage: string;
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  icons?: ReadonlyArray<ImageSource>;
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  scrollEdgeAppearance?: string;
  barTintColor?: ProcessedColorValue | null;
  translucent?: boolean;
  rippleColor?: ProcessedColorValue | null;
  activeTintColor?: ProcessedColorValue | null;
  inactiveTintColor?: ProcessedColorValue | null;
}

export type TabViewNativeProps = Omit<
  TabViewProps,
  'activeTintColor' | 'inactiveTintColor'
> & {
  activeTintColor?: ColorValue;
  inactiveTintColor?: ColorValue;
};

export default codegenNativeComponent<TabViewProps>('RCTTabView');
