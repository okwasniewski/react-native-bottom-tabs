import type {
  DefaultNavigatorOptions,
  ParamListBase,
  TabActionHelpers,
  TabNavigationState,
  TabRouterOptions,
} from '@react-navigation/native';
import {
  TabRouter,
  createNavigatorFactory,
  useNavigationBuilder,
  useTheme,
} from '@react-navigation/native';
import Color from 'color';

import type {
  NativeBottomTabNavigationConfig,
  NativeBottomTabNavigationEventMap,
  NativeBottomTabNavigationOptions,
} from '../types';
import NativeBottomTabView from '../views/NativeBottomTabView';

export type NativeBottomTabNavigatorProps = DefaultNavigatorOptions<
  ParamListBase,
  TabNavigationState<ParamListBase>,
  NativeBottomTabNavigationOptions,
  NativeBottomTabNavigationEventMap
> &
  TabRouterOptions &
  NativeBottomTabNavigationConfig;

function NativeBottomTabNavigator({
  id,
  initialRouteName,
  backBehavior,
  children,
  screenListeners,
  screenOptions,
  tabBarActiveTintColor: customActiveTintColor,
  tabBarInactiveTintColor: customInactiveTintColor,
  ...rest
}: NativeBottomTabNavigatorProps) {
  const { colors } = useTheme();
  const activeTintColor =
    customActiveTintColor === undefined
      ? colors.primary
      : customActiveTintColor;

  const inactiveTintColor =
    customInactiveTintColor === undefined
      ? Color(colors.text).mix(Color(colors.card), 0.5).hex()
      : customInactiveTintColor;

  const { state, descriptors, navigation, NavigationContent } =
    useNavigationBuilder<
      TabNavigationState<ParamListBase>,
      TabRouterOptions,
      TabActionHelpers<ParamListBase>,
      NativeBottomTabNavigationOptions,
      NativeBottomTabNavigationEventMap
    >(TabRouter, {
      id,
      initialRouteName,
      backBehavior,
      children,
      screenListeners,
      screenOptions,
    });

  return (
    <NavigationContent>
      <NativeBottomTabView
        {...rest}
        tabBarActiveTintColor={activeTintColor}
        tabBarInactiveTintColor={inactiveTintColor}
        state={state}
        navigation={navigation}
        descriptors={descriptors}
      />
    </NavigationContent>
  );
}

export default createNavigatorFactory<
  TabNavigationState<ParamListBase>,
  NativeBottomTabNavigationOptions,
  NativeBottomTabNavigationEventMap,
  typeof NativeBottomTabNavigator
>(NativeBottomTabNavigator);
