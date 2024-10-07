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
} from '@react-navigation/native';

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
  ...rest
}: NativeBottomTabNavigatorProps) {
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
