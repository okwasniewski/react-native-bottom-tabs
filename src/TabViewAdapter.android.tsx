import NativeTabView from './TabViewNativeComponent';
import type { TabViewProps } from './TabViewNativeComponent';
import { StyleSheet, View } from 'react-native';

const TabViewAdapter = ({ children, ...props }: TabViewProps) => {
  return (
    <>
      <View style={styles.content}>{children}</View>
      <NativeTabView {...props} style={styles.tabBar} />
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
