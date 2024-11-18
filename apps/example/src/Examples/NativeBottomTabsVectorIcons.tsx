import { Article } from '../Screens/Article';
import { Albums } from '../Screens/Albums';
import { Contacts } from '../Screens/Contacts';
import { Chat } from '../Screens/Chat';
import { createNativeBottomTabNavigator } from '@bottom-tabs/react-navigation';
import { Platform } from 'react-native';

import Icon from 'react-native-vector-icons/MaterialCommunityIcons';

const accountIcon = Icon.getImageSourceSync('account', 24);
const accountIconAlert = Icon.getImageSourceSync('account-alert-outline', 24);
const noteIcon = Icon.getImageSourceSync('note', 24);
const gridIcon = Icon.getImageSourceSync('grid-large', 24);
const messageIcon = Icon.getImageSourceSync('message', 24);

const Tab = createNativeBottomTabNavigator();

function NativeBottomTabsVectorIcons() {
  return (
    <Tab.Navigator
      tabBarInactiveTintColor="#C57B57"
      tabBarActiveTintColor="#F7DBA7"
      barTintColor="#1E2D2F"
      rippleColor="#F7DBA7"
      activeIndicatorColor="#041F1E"
      screenListeners={{
        tabLongPress: (data) => {
          console.log(
            `${Platform.OS}: Long press detected on tab with key ${data.target} at the navigator level.`
          );
        },
      }}
    >
      <Tab.Screen
        name="Article"
        component={Article}
        listeners={{
          tabLongPress: (data) => {
            console.log(
              `${Platform.OS}: Long press detected on tab with key ${data.target} at the screen level.`
            );
          },
        }}
        options={{
          tabBarBadge: '10',
          tabBarIcon: () => noteIcon,
        }}
      />
      <Tab.Screen
        name="Albums"
        component={Albums}
        options={{
          tabBarIcon: () => gridIcon,
        }}
      />
      <Tab.Screen
        name="Contacts"
        component={Contacts}
        options={{
          tabBarIcon: ({ focused }) =>
            focused ? accountIcon : accountIconAlert,
          tabBarActiveTintColor: 'yellow',
        }}
      />
      <Tab.Screen
        name="Chat"
        component={Chat}
        options={{
          tabBarIcon: () => messageIcon,
          tabBarActiveTintColor: 'white',
          tabBarItemHidden: true,
        }}
      />
    </Tab.Navigator>
  );
}

export default NativeBottomTabsVectorIcons;
