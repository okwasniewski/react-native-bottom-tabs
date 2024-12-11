import type {
  Descriptor,
  NavigationHelpers,
  NavigationProp,
  ParamListBase,
  RouteProp,
  TabActionHelpers,
  TabNavigationState,
} from '@react-navigation/native';
import type { ImageSourcePropType } from 'react-native';
import type TabView from '../TabView';
import type { AppleIcon } from '../types';

export type NativeBottomTabNavigationEventMap = {
  /**
   * Event which fires on tapping on the tab in the tab bar.
   */
  tabPress: { data: undefined };
  /**
   * Event which fires on long press on tab bar.
   */
  tabLongPress: { data: undefined };
};

export type NativeBottomTabNavigationHelpers = NavigationHelpers<
  ParamListBase,
  NativeBottomTabNavigationEventMap
> &
  TabActionHelpers<ParamListBase>;

export type NativeBottomTabNavigationProp<
  ParamList extends ParamListBase,
  RouteName extends keyof ParamList = keyof ParamList,
  NavigatorID extends string | undefined = undefined,
> = NavigationProp<
  ParamList,
  RouteName,
  NavigatorID,
  TabNavigationState<ParamList>,
  NativeBottomTabNavigationOptions,
  NativeBottomTabNavigationEventMap
> &
  TabActionHelpers<ParamList>;

export type NativeBottomTabScreenProps<
  ParamList extends ParamListBase,
  RouteName extends keyof ParamList = keyof ParamList,
  NavigatorID extends string | undefined = undefined,
> = {
  navigation: NativeBottomTabNavigationProp<ParamList, RouteName, NavigatorID>;
  route: RouteProp<ParamList, RouteName>;
};

export type NativeBottomTabNavigationOptions = {
  /**
   * Title text for the screen.
   */
  title?: string;

  /**
   * Label text of the tab displayed in the navigation bar. When undefined, scene title is used.
   */
  tabBarLabel?: string;

  /**
   * Function that given { focused: boolean } returns ImageSource or AppleIcon to display in the navigation bar.
   */
  tabBarIcon?: (props: { focused: boolean }) => ImageSourcePropType | AppleIcon;

  /**
   * Badge to show on the tab icon.
   */
  tabBarBadge?: string;

  /**
   * Whether this screens should render the first time it's accessed. Defaults to true. Set it to false if you want to render the screen on initial render.
   */
  lazy?: boolean;

  /**
   * Active tab color.
   */
  tabBarActiveTintColor?: string;

  /**
   * Whether inactive screens should be suspended from re-rendering. Defaults to `false`.
   * Defaults to `true` when `enableFreeze()` is run at the top of the application.
   * Requires `react-native-screens` version >=3.16.0.
   *
   * Only supported on iOS and Android.
   */
  freezeOnBlur?: boolean;
};

export type NativeBottomTabDescriptor = Descriptor<
  NativeBottomTabNavigationOptions,
  NativeBottomTabNavigationProp<ParamListBase>,
  RouteProp<ParamListBase>
>;

export type NativeBottomTabDescriptorMap = Record<
  string,
  NativeBottomTabDescriptor
>;

export type NativeBottomTabNavigationConfig = Partial<
  Omit<
    React.ComponentProps<typeof TabView>,
    | 'navigationState'
    | 'onIndexChange'
    | 'renderScene'
    | 'getLazy'
    | 'getIcon'
    | 'getLabelText'
    | 'getBadge'
    | 'onTabLongPress'
    | 'getActiveTintColor'
  >
>;