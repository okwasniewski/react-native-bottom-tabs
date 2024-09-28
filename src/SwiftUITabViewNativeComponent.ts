import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

export type OnPageSelectedEventData = Readonly<{
  key: string;
}>;

export type TabViewItems = ReadonlyArray<{
  key: string;
  title: string;
  icon: string;
  badge?: string;
}>;

export interface TabViewProps extends ViewProps {
  items: TabViewItems;
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  selectedPage: string;
}

export default codegenNativeComponent<TabViewProps>('SwiftUITabViewView', {
  excludedPlatforms: ['android'],
});
