import NativeTabView from './TabViewNativeComponent';
import type { TabViewNativeProps } from './TabViewNativeComponent';
import { processColor, StyleSheet, View } from 'react-native';

const TabViewAdapter = ({
  children,
  activeTintColor,
  inactiveTintColor,
  ...props
}: TabViewNativeProps) => {
  return (
    <>
      <View style={styles.content}>{children}</View>
      <NativeTabView
        {...props}
        activeTintColor={processColor(activeTintColor)}
        inactiveTintColor={processColor(inactiveTintColor)}
        style={styles.tabBar}
      />
    </>
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
  tabBar: {},
});

export default TabViewAdapter;
