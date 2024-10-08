import { withLayoutContext } from 'expo-router';

import {
  createNativeBottomTabNavigator,
  NativeBottomTabNavigationEventMap,
  NativeBottomTabNavigationOptions,
} from 'react-native-bottom-tabs/react-navigation';

// This should be imported from react-native-bottom-tabs/react-navigation which is
// exporting NativeBottomTabNavigationOptions but the types seem to be broken at
// at the moment..
// import { BottomTabNavigationOptions } from '@react-navigation/bottom-tabs';

import type {
  ParamListBase,
  TabNavigationState,
} from '@react-navigation/native';

const { Navigator } = createNativeBottomTabNavigator();

export const NativeTabs = withLayoutContext<
  NativeBottomTabNavigationOptions,
  typeof Navigator,
  TabNavigationState<ParamListBase>,
  NativeBottomTabNavigationEventMap
>(Navigator);
