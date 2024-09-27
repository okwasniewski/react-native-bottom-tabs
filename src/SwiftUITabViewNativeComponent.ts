import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';

export interface TabViewProps extends ViewProps {
  items: {};
}

export default codegenNativeComponent<TabViewProps>('SwiftUITabViewView');
