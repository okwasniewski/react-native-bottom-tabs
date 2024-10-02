import React from 'react';
import type { TabViewItems } from './TabViewNativeComponent';
import { Platform, StyleSheet, View } from 'react-native';
import TabViewAdapter from './TabViewAdapter';
import useLatestCallback from 'use-latest-callback';

export type BaseRoute = {
  key: string;
  title?: string;
  badge?: string;
  lazy?: boolean;
  icon?: string;
};

type NavigationState<Route extends BaseRoute> = {
  index: number;
  routes: Route[];
};

interface Props<Route extends BaseRoute> {
  navigationState: NavigationState<Route>;
  renderScene: (props: {
    route: Route;
    jumpTo: (key: string) => void;
  }) => React.ReactNode | null;
  /**
   * Callback which is called on tab change, receives the index of the new tab as argument.
   * The navigation state needs to be updated when it's called, otherwise the change is dropped.
   */
  onIndexChange: (index: number) => void;
  /**
   * Get lazy for the current screen. Uses true by default.
   */
  getLazy?: (props: { route: Route }) => boolean | undefined;
}

const TabView = <Route extends BaseRoute>({
  navigationState,
  renderScene,
  onIndexChange,
  getLazy = ({ route }: { route: Route }) => route.lazy,
  ...props
}: Props<Route>) => {
  // @ts-ignore
  const focusedKey = navigationState.routes[navigationState.index].key;

  /**
   * List of loaded tabs, tabs will be loaded when navigated to.
   */
  const [loaded, setLoaded] = React.useState<string[]>([focusedKey]);

  if (!loaded.includes(focusedKey)) {
    // Set the current tab to be loaded if it was not loaded before
    setLoaded((loaded) => [...loaded, focusedKey]);
  }

  const items: TabViewItems = navigationState.routes.map((route) => ({
    key: route.key,
    title: route.title ?? route.key,
    icon: route.icon,
    badge: route.badge,
  }));

  const jumpTo = useLatestCallback((key: string) => {
    const index = navigationState.routes.findIndex(
      (route) => route.key === key
    );

    onIndexChange(index);
  });

  return (
    <TabViewAdapter
      style={styles.fullWidth}
      items={items}
      selectedPage={focusedKey}
      onPageSelected={({ nativeEvent: { key } }) => {
        const index = navigationState.routes.findIndex((r) => r.key === key);

        if (index !== -1) {
          onIndexChange?.(index);
        }
      }}
      {...props}
    >
      {navigationState.routes.map((route) => {
        if (getLazy({ route }) !== false && !loaded.includes(route.key)) {
          // Don't render a screen if we've never navigated to it
          if (Platform.OS === 'android') {
            return null;
          }
          return <View key={route.key} />;
        }

        return (
          <View
            key={route.key}
            style={[
              { width: '100%', height: '100%' },
              Platform.OS === 'android' && {
                display: route.key === focusedKey ? 'flex' : 'none',
              },
            ]}
          >
            {renderScene({
              route,
              jumpTo,
            })}
          </View>
        );
      })}
    </TabViewAdapter>
  );
};

const styles = StyleSheet.create({
  fullWidth: {
    width: '100%',
    height: '100%',
  },
});

export default TabView;
