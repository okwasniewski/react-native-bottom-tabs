import type { TabViewItems } from './TabViewNativeComponent';
import { Image, Platform, StyleSheet, View } from 'react-native';

//@ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';
import TabViewAdapter from './TabViewAdapter';
import useLatestCallback from 'use-latest-callback';
import { useMemo, useState } from 'react';
import type { BaseRoute, NavigationState } from './types';

const isAppleSymbol = (icon: any): icon is { sfSymbol: string } =>
  icon?.sfSymbol;

interface Props<Route extends BaseRoute> {
  /*
   * Whether to show labels in tabs. When false, only icons will be displayed.
   */
  labeled?: boolean;
  /**
   * A tab bar style that adapts to each platform.
   *
   * that varies depending on the platform:
   * Tab views using the sidebar adaptable style have an appearance
   * - iPadOS displays a top tab bar that can adapt into a sidebar.
   * - iOS displays a bottom tab bar.
   * - macOS and tvOS always show a sidebar.
   * - visionOS shows an ornament and also shows a sidebar for secondary tabs within a `TabSection`.
   */
  sidebarAdaptable?: boolean;
  /**
   * Whether to ignore the top safe area. (iOS only)
   */
  ignoresTopSafeArea?: boolean;

  /**
   * Whether to disable page animations between tabs. (iOS only)
   */
  disablePageAnimations?: boolean;
  /**
   * State for the tab view.
   *
   * The state should contain a `routes` prop which is an array of objects containing `key` and `title` props, such as `{ key: 'music', title: 'Music' }`.
   *
   */
  navigationState: NavigationState<Route>;
  /**
   * Function which takes an object with the route and returns a React element.
   */
  renderScene: (props: {
    route: Route;
    jumpTo: (key: string) => void;
  }) => React.ReactNode | null;
  /**
   * Callback which is called on tab change, receives the index of the new tab as argument.
   */
  onIndexChange: (index: number) => void;
  /**
   * Get lazy for the current screen. Uses true by default.
   */
  getLazy?: (props: { route: Route }) => boolean | undefined;
  /**
   * Get label text for the tab, uses `route.title` by default.
   */
  getLabelText?: (props: { route: Route }) => string | undefined;
  /**
   * Get badge for the tab, uses `route.badge` by default.
   */
  getBadge?: (props: { route: Route }) => string | undefined;
  /**
   * Get icon for the tab, uses `route.focusedIcon` by default.
   */
  getIcon?: (props: {
    route: Route;
    focused: boolean;
  }) => ImageSource | undefined;
}

const ANDROID_MAX_TABS = 6;

const TabView = <Route extends BaseRoute>({
  navigationState,
  renderScene,
  onIndexChange,
  getLazy = ({ route }: { route: Route }) => route.lazy,
  getLabelText = ({ route }: { route: Route }) => route.title,
  getIcon = ({ route, focused }: { route: Route; focused: boolean }) =>
    route.unfocusedIcon
      ? focused
        ? route.focusedIcon
        : route.unfocusedIcon
      : route.focusedIcon,
  ...props
}: Props<Route>) => {
  // @ts-ignore
  const focusedKey = navigationState.routes[navigationState.index].key;

  const trimmedRoutes = useMemo(() => {
    if (
      Platform.OS === 'android' &&
      navigationState.routes.length > ANDROID_MAX_TABS
    ) {
      console.warn(
        `TabView only supports up to ${ANDROID_MAX_TABS} tabs on Android`
      );
      return navigationState.routes.slice(0, ANDROID_MAX_TABS);
    }
    return navigationState.routes;
  }, [navigationState.routes]);

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
      trimmedRoutes.map((route) =>
        getIcon({
          route,
          focused: route.key === focusedKey,
        })
      ),
    [focusedKey, getIcon, trimmedRoutes]
  );

  const items: TabViewItems = useMemo(
    () =>
      trimmedRoutes.map((route, index) => {
        const icon = icons[index];
        const isSfSymbol = isAppleSymbol(icon);

        if (Platform.OS === 'android' && isSfSymbol) {
          console.warn(
            'SF Symbols are not supported on Android. Use require() or pass uri to load an image instead.'
          );
        }
        return {
          key: route.key,
          title: getLabelText({ route }) ?? route.key,
          sfSymbol: isSfSymbol ? icon.sfSymbol : undefined,
          badge: props.getBadge?.({ route }),
        };
      }),
    [getLabelText, icons, trimmedRoutes, props]
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

  const jumpTo = useLatestCallback((key: string) => {
    const index = trimmedRoutes.findIndex((route) => route.key === key);

    onIndexChange(index);
  });

  return (
    <TabViewAdapter
      style={styles.fullWidth}
      items={items}
      icons={resolvedIconAssets}
      selectedPage={focusedKey}
      onPageSelected={({ nativeEvent: { key } }) => {
        jumpTo(key);
      }}
      {...props}
    >
      {trimmedRoutes.map((route) => {
        if (getLazy({ route }) !== false && !loaded.includes(route.key)) {
          // Don't render a screen if we've never navigated to it
          if (Platform.OS === 'android') {
            return null;
          }
          return <View key={route.key} style={styles.fullWidth} />;
        }

        return (
          <View
            key={route.key}
            style={[
              styles.fullWidth,
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
