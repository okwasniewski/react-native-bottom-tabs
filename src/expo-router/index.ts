import { withLayoutContext } from 'expo-router';

import {
  createNativeBottomTabNavigator,
  NativeBottomTabNavigationEventMap,
  NativeBottomTabNavigationOptions,
} from 'react-native-bottom-tabs/react-navigation';

import type {
  ParamListBase,
  TabNavigationState,
} from '@react-navigation/native';

export const NativeTabs = withLayoutContext<
  NativeBottomTabNavigationOptions,
  ReturnType<typeof createNativeBottomTabNavigator>['Navigator'],
  TabNavigationState<ParamListBase>,
  NativeBottomTabNavigationEventMap
>(createNativeBottomTabNavigator().Navigator);
