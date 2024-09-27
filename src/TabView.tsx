import React, { type ReactNode } from 'react';
import SwiftUITabViewView from './SwiftUITabViewNativeComponent';
import type { TabViewProps } from './SwiftUITabViewNativeComponent';
import { View } from 'react-native';

export type TabViewItems = Record<
  string,
  {
    title: string;
    icon: string;
    badge?: string;
  }
>;

interface Props extends TabViewProps {
  items: TabViewItems;
}

const childrenWithOverriddenStyle = (children?: ReactNode, pageMargin = 0) => {
  return React.Children.map(children, (child) => {
    return (
      <View
        style={{
          height: '100%',
          width: '100%',
          paddingHorizontal: pageMargin / 2,
        }}
        collapsable={false}
      >
        {child}
      </View>
    );
  });
};

const TabView = ({ children, ...props }: Props) => {
  return (
    <SwiftUITabViewView
      {...props}
      children={childrenWithOverriddenStyle(children, 0)}
    />
  );
};

export default TabView;
