import React, { type ReactNode } from 'react';
import NativeTabView from './TabViewNativeComponent';
import type { TabViewProps } from './TabViewNativeComponent';
import { View } from 'react-native';

interface Props extends TabViewProps {}

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
    <NativeTabView
      {...props}
      children={childrenWithOverriddenStyle(children, 0)}
    />
  );
};

export default TabView;
