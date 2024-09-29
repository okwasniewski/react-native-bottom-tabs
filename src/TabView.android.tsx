import React, { useMemo } from 'react';
import NativeTabView from './TabViewNativeComponent';
import type { TabViewProps } from './TabViewNativeComponent';
import { StyleSheet, View } from 'react-native';

interface Props extends TabViewProps {}

const TabView = ({ children, ...props }: Props) => {
  const items = props.items;
  const selectedPage = props.selectedPage;

  const renderedChildren = useMemo(
    () =>
      React.Children.map(children, (child, index) => {
        const key = items?.[index]?.key;
        return (
          <View
            key={key}
            style={[
              styles.tabContent,
              { display: key === selectedPage ? 'flex' : 'none' },
            ]}
          >
            {child}
          </View>
        );
      }),
    [children, items, selectedPage]
  );
  return (
    <View style={styles.container}>
      <View style={styles.content}>{renderedChildren}</View>
      <NativeTabView {...props} style={styles.tabBar} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
  },
  content: {
    flex: 1,
  },
  tabContent: {
    flex: 1,
    width: '100%',
    height: '100%',
  },
  tabBar: {
    minHeight: 81,
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
  },
});

export default TabView;
