import React from 'react';

import { withLayoutContext } from 'expo-router';
import createNativeBottomTabNavigator from '../../../../packages/react-native-bottom-tabs/src/react-navigation/navigators/createNativeBottomTabNavigator';

const Tabs = withLayoutContext(createNativeBottomTabNavigator().Navigator);

function TabLayout() {
  return (
    <Tabs>
      <Tabs.Screen
        name="index"
        options={{
          title: 'Home',
          tabBarIcon: () => ({ sfSymbol: 'house.fill' }),
        }}
      />
      <Tabs.Screen
        name="explore"
        options={{
          title: 'Explore',
          tabBarIcon: () => ({ sfSymbol: 'house.fill' }),
        }}
      />
    </Tabs>
  );
}

export default TabLayout;
