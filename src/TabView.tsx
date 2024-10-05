import type { TabViewConfig, TabViewItems } from './TabViewNativeComponent';
import { Image, Platform, StyleSheet, View } from 'react-native';

//@ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';
import TabViewAdapter from './TabViewAdapter';
import useLatestCallback from 'use-latest-callback';
import { useMemo, useState } from 'react';
import type { BaseRoute, NavigationState } from './types';

const isAppleSymbol = (icon: any): icon is { sfSymbol: string } =>
  icon.sfSymbol;

interface Props<Route extends BaseRoute> {
  /*
   * Whether to show labels in tabs. When false, only icons will be displayed.
   */
  labeled?: boolean;
  /*
   * Whether to show labels in tabs. When false, only icons will be displayed.
   */
  sidebarAdaptable?: boolean;
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

  if (navigationState.routes.length > 6) {
    throw new Error('TabView only supports up to 6 tabs');
  }

  /**
   * List of loaded tabs, tabs will be loaded when navigated to.
   */
  const [loaded, setLoaded] = useState<string[]>([focusedKey]);

  if (!loaded.includes(focusedKey)) {
    // Set the current tab to be loaded if it was not loaded before
    setLoaded((loaded) => [...loaded, focusedKey]);
  }

  const icons = useMemo(
    () =>
      navigationState.routes.map((route) =>
        route.unfocusedIcon
          ? route.key === focusedKey
            ? route.focusedIcon
            : route.unfocusedIcon
          : route.focusedIcon
      ),
    [focusedKey, navigationState.routes]
  );

  const items: TabViewItems = useMemo(
    () =>
      navigationState.routes.map((route, index) => {
        const icon = icons[index];
        const isSfSymbol = isAppleSymbol(icon);

        if (Platform.OS === 'android' && isSfSymbol) {
          console.warn(
            'SF Symbols are not supported on Android. Use require() or pass uri to load an image instead.'
          );
        }
        return {
          key: route.key,
          title: route.title ?? route.key,
          sfSymbol: isSfSymbol ? icon.sfSymbol : undefined,
          badge: route.badge,
        };
      }),
    [icons, navigationState.routes]
  );

  const resolvedIconAssets: ImageSource[] = useMemo(
    () =>
      // Pass empty object for icons that are not provided to avoid index mismatch on native side.
      icons.map((icon) =>
        icon && !isAppleSymbol(icon)
          ? Image.resolveAssetSource(icon)
          : { uri: '' }
      ),
    [icons]
  );

  const nativeConfig: TabViewConfig = useMemo(
    () => ({
      labeled: props.labeled,
      sidebarAdaptable: props.sidebarAdaptable,
    }),
    [props.labeled, props.sidebarAdaptable]
  );

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
      icons={resolvedIconAssets}
      selectedPage={focusedKey}
      config={nativeConfig}
      onPageSelected={({ nativeEvent: { key } }) => {
        jumpTo(key);
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
