import {
  type ParamListBase,
  type TabNavigationState,
  type Route,
  CommonActions,
} from '@react-navigation/native';
import type {
  NativeBottomTabDescriptorMap,
  NativeBottomTabNavigationConfig,
  NativeBottomTabNavigationHelpers,
} from '../types';
import TabView from 'react-native-bottom-tabs';

type Props = NativeBottomTabNavigationConfig & {
  state: TabNavigationState<ParamListBase>;
  navigation: NativeBottomTabNavigationHelpers;
  descriptors: NativeBottomTabDescriptorMap;
};

export default function NativeBottomTabView({
  state,
  navigation,
  descriptors,
  ...rest
}: Props) {
  return (
    <TabView
      {...rest}
      navigationState={state}
      renderScene={({ route }) => descriptors[route.key]?.render()}
      getActiveTintColor={({ route }) => {
        return descriptors[route.key]?.options.tabBarActiveTintColor;
      }}
      getLabelText={({ route }) => {
        const options = descriptors[route.key]?.options;

        return options?.tabBarLabel !== undefined
          ? options.tabBarLabel
          : options?.title !== undefined
            ? options.title
            : (route as Route<string>).name;
      }}
      getBadge={({ route }) => descriptors[route.key]?.options.tabBarBadge}
      getHidden={({ route }) => {
        const options = descriptors[route.key]?.options;
        return options?.tabBarItemHidden === true;
      }}
      getTestID={({ route }) =>
        descriptors[route.key]?.options.tabBarButtonTestID
      }
      getIcon={({ route, focused }) => {
        const options = descriptors[route.key]?.options;

        if (options?.tabBarIcon) {
          const { tabBarIcon } = options;
          return tabBarIcon({ focused });
        }

        return null;
      }}
      getLazy={({ route }) => descriptors[route.key]?.options.lazy ?? true}
      onTabLongPress={(index) => {
        const route = state.routes[index];
        if (!route) {
          return;
        }

        navigation.emit({
          type: 'tabLongPress',
          target: route.key,
        });
      }}
      onIndexChange={(index) => {
        const route = state.routes[index];
        if (!route) {
          return;
        }

        const event = navigation.emit({
          type: 'tabPress',
          target: route.key,
          canPreventDefault: true,
        });

        if (event.defaultPrevented) {
          return;
        } else {
          navigation.dispatch({
            ...CommonActions.navigate(route),
            target: state.key,
          });
        }
      }}
    />
  );
}
