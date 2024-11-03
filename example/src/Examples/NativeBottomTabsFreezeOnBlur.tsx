import * as React from 'react';
import { Text, View } from 'react-native';
import createNativeBottomTabNavigator from '../../../src/react-navigation/navigators/createNativeBottomTabNavigator';
// import { enableFreeze } from 'react-native-screens';

const store = new Set<Dispatch>();
// enableFreeze(true)

type Dispatch = (value: number) => void;

function useValue() {
  const [value, setValue] = React.useState<number>(0); // integer state

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
        borderWidth: 2,
        borderColor: 'blue',
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
  const random = React.useRef(Math.random());
  // only 1 'render' should appear at the time
  console.log('Details Screen render', value, random);
  return (
    <View
      style={{
        borderWidth: 2,
        borderColor: 'pink',
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <Text>Details!</Text>
      <Text style={{ alignSelf: 'center' }}>
        Details Screen {value + random.current}
      </Text>
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
    <Tab.Navigator screenOptions={{}}>
      <Tab.Screen name="Home" component={HomeScreen} />
      <Tab.Screen name="Details" component={DetailsScreen} />
      <Tab.Screen name="Settings" component={DetailsScreen} />
      <Tab.Screen name="Profile" component={DetailsScreen} />
    </Tab.Navigator>
  );
}
