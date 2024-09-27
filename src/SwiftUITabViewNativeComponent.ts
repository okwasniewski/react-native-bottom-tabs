import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

export type OnPageSelectedEventData = Readonly<{
  key: string;
}>;

export interface TabViewProps extends ViewProps {
  items: {};
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  selectedPage: string;
}

export default codegenNativeComponent<TabViewProps>('SwiftUITabViewView');
