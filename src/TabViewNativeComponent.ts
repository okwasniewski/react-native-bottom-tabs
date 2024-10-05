import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';
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

export type TabViewConfig = {
  labeled?: boolean;
  sidebarAdaptable?: boolean;
};

export interface TabViewProps extends ViewProps {
  items: TabViewItems;
  selectedPage: string;
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  icons?: ReadonlyArray<ImageSource>;
  config?: TabViewConfig;
}

export default codegenNativeComponent<TabViewProps>('RCTTabView');
