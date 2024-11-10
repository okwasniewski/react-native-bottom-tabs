import NativeTabView from './TabViewNativeComponent';
import type { TabViewProps } from './TabViewNativeComponent';
import { StyleSheet, View } from 'react-native';

const TabViewAdapter = ({ children, style: _, ...props }: TabViewProps) => {
  const hidesTabBar = props.items.find(
    (item) => item.key === props.selectedPage
  )?.tabBarHidden;

  return (
    <>
      <View style={styles.content}>{children}</View>
      <NativeTabView
        {...props}
        style={{ display: hidesTabBar ? 'none' : 'flex' }}
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
});

export default TabViewAdapter;
