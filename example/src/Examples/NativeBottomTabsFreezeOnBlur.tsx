import * as React from 'react';
import { Text, View } from 'react-native';
import createNativeBottomTabNavigator from '../../../src/react-navigation/navigators/createNativeBottomTabNavigator';

const store = new Set<Dispatch>();

type Dispatch = (value: number) => void;

function useValue() {
  const [value, setValue] = React.useState<number>(0);

  React.useEffect(() => {
    const dispatch = (value: number) => {
      setValue(value);
    };
    store.add(dispatch);
    return () => {
      store.delete(dispatch);
    };
  }, [setValue]);

  return value;
}

function HomeScreen() {
  return (
    <View
      style={{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <Text>Home!</Text>
    </View>
  );
}

function DetailsScreen() {
  const value = useValue();
  // only 1 'render' should appear at the time
  console.log('Details Screen render', value);
  return (
    <View
      style={{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <Text>Details!</Text>
      <Text style={{ alignSelf: 'center' }}>Details Screen {value}</Text>
    </View>
  );
}
const Tab = createNativeBottomTabNavigator();

export default function NativeBottomTabsFreezeOnBlur() {
  React.useEffect(() => {
    let timer = 0;
    const interval = setInterval(() => {
      timer = timer + 1;
      store.forEach((dispatch) => dispatch(timer));
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <Tab.Navigator
      screenOptions={{
        freezeOnBlur: true,
      }}
    >
      <Tab.Screen
        name="Article"
        component={HomeScreen}
        options={{
          tabBarIcon: () => require('../../assets/icons/article_dark.png'),
        }}
      />
      <Tab.Screen
        name="Albums"
        component={DetailsScreen}
        options={{
          tabBarIcon: () => require('../../assets/icons/grid_dark.png'),
        }}
      />
      <Tab.Screen
        name="Contact"
        component={DetailsScreen}
        options={{
          tabBarIcon: () => require('../../assets/icons/person_dark.png'),
        }}
      />
      <Tab.Screen
        name="Chat"
        component={DetailsScreen}
        options={{
          tabBarIcon: () => require('../../assets/icons/chat_dark.png'),
        }}
      />
    </Tab.Navigator>
  );
}
