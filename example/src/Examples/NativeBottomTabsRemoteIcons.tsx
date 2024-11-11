import { Article } from '../Screens/Article';
import { Albums } from '../Screens/Albums';
import { Contacts } from '../Screens/Contacts';
import { Chat } from '../Screens/Chat';
// This import works properly when library is published
import createNativeBottomTabNavigator from '../../../src/react-navigation/navigators/createNativeBottomTabNavigator';

const Tab = createNativeBottomTabNavigator();

function NativeBottomTabsRemoteIcons() {
  return (
    <Tab.Navigator labeled>
      <Tab.Screen
        name="Article"
        component={Article}
        options={{
          tabBarBadge: '10',
          tabBarIcon: () => ({
            uri: 'https://www.svgrepo.com/show/533824/water-container.svg',
          }),
        }}
      />
      <Tab.Screen
        name="Albums"
        component={Albums}
        options={{
          tabBarIcon: () => ({
            uri: 'https://www.svgrepo.com/show/533813/hat-chef.svg',
          }),
        }}
      />
      <Tab.Screen
        name="Contacts"
        component={Contacts}
        options={{
          tabBarIcon: () => ({
            uri: 'https://www.svgrepo.com/show/533826/shop.svg',
          }),
        }}
      />
      <Tab.Screen
        name="Chat"
        component={Chat}
        options={{
          tabBarIcon: () => ({
            uri: 'https://www.svgrepo.com/show/533828/cheese.svg',
          }),
        }}
      />
    </Tab.Navigator>
  );
}

export default NativeBottomTabsRemoteIcons;
