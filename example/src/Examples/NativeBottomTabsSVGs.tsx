import { Article } from '../Screens/Article';
import { Albums } from '../Screens/Albums';
import { Contacts } from '../Screens/Contacts';
import { Chat } from '../Screens/Chat';
// This import works properly when library is published
import createNativeBottomTabNavigator from '../../../src/react-navigation/navigators/createNativeBottomTabNavigator';

const Tab = createNativeBottomTabNavigator();

function NativeBottomTabsSVGs() {
  return (
    <Tab.Navigator>
      <Tab.Screen
        name="Article"
        component={Article}
        options={{
          tabBarBadge: '10',
          tabBarIcon: () => require('../../assets/icons/newspaper.svg'),
        }}
      />
      <Tab.Screen
        name="Albums"
        component={Albums}
        options={{
          tabBarIcon: () => require('../../assets/icons/book-image.svg'),
        }}
      />
      <Tab.Screen
        name="Contacts"
        component={Contacts}
        options={{
          tabBarIcon: ({ focused }) =>
            focused
              ? require('../../assets/icons/user-round-search.svg')
              : require('../../assets/icons/user-round.svg'),
        }}
      />
      <Tab.Screen
        name="Chat"
        component={Chat}
        options={{
          tabBarIcon: () =>
            require('../../assets/icons/message-circle-code.svg'),
        }}
      />
    </Tab.Navigator>
  );
}

export default NativeBottomTabsSVGs;
