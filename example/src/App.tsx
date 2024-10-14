import { enableScreens } from 'react-native-screens';
// run this before any screen render(usually in App.js)
enableScreens();

import * as React from 'react';
import {
  StyleSheet,
  Text,
  ScrollView,
  TouchableOpacity,
  Button,
  Alert,
  useColorScheme,
} from 'react-native';
import {
  DarkTheme,
  DefaultTheme,
  NavigationContainer,
  useNavigation,
} from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import JSBottomTabs from './Examples/JSBottomTabs';
import ThreeTabs from './Examples/ThreeTabs';
import FourTabs from './Examples/FourTabs';
import MaterialBottomTabs from './Examples/MaterialBottomTabs';
import SFSymbols from './Examples/SFSymbols';
import LabeledTabs from './Examples/Labeled';
import NativeBottomTabs from './Examples/NativeBottomTabs';
import TintColorsExample from './Examples/TintColors';

const FourTabsIgnoreSafeArea = () => {
  return <FourTabs ignoresTopSafeArea />;
};

const FourTabsRippleColor = () => {
  return <FourTabs rippleColor={'#00ff00'} />;
};

const FourTabsNoAnimations = () => {
  return <FourTabs disablePageAnimations />;
};

const FourTabsTransparentScrollEdgeAppearance = () => {
  return <FourTabs scrollEdgeAppearance="transparent" />;
};

const FourTabsWithBarTintColor = () => {
  return <FourTabs barTintColor={'#87CEEB'} />;
};

const FourTabsTranslucent = () => {
  return <FourTabs translucent={false} />;
};

const examples = [
  { component: ThreeTabs, name: 'Three Tabs' },
  { component: FourTabs, name: 'Four Tabs' },
  { component: SFSymbols, name: 'SF Symbols' },
  { component: LabeledTabs, name: 'Labeled Tabs' },
  {
    component: FourTabsIgnoreSafeArea,
    name: 'Four Tabs - No header',
    screenOptions: { headerShown: false },
  },
  {
    component: FourTabsRippleColor,
    name: 'Four Tabs with ripple Color',
  },
  { component: FourTabsNoAnimations, name: 'Four Tabs - no animations' },
  {
    component: FourTabsTransparentScrollEdgeAppearance,
    name: 'Four Tabs - Transparent scroll edge appearance',
  },
  {
    component: FourTabsWithBarTintColor,
    name: 'Four Tabs - Custom Background Color of Tabs',
  },
  {
    component: FourTabsTranslucent,
    name: 'Four Tabs - Translucent tab bar',
  },
  { component: NativeBottomTabs, name: 'Native Bottom Tabs' },
  { component: JSBottomTabs, name: 'JS Bottom Tabs' },
  {
    component: JSBottomTabs,
    name: 'JS Bottom Tabs - No header',
    screenOptions: { headerShown: false },
  },
  { component: MaterialBottomTabs, name: 'Material (JS) Bottom Tabs' },
  { component: TintColorsExample, name: 'Tint Colors' },
];

function App() {
  const navigation = useNavigation();
  return (
    <ScrollView>
      {examples.map((example) => (
        <TouchableOpacity
          key={example.name}
          testID={example.name}
          style={styles.exampleTouchable}
          onPress={() => {
            //@ts-ignore
            navigation.navigate(example.name);
          }}
        >
          <Text style={styles.exampleText}>{example.name}</Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );
}

const Stack = createStackNavigator();

const NativeStack = createNativeStackNavigator();

export default function Navigation() {
  const [mode, setMode] = React.useState<'native' | 'js'>('native');
  const NavigationStack = mode === 'js' ? Stack : NativeStack;
  const colorScheme = useColorScheme();
  const theme = colorScheme === 'dark' ? DarkTheme : DefaultTheme;

  return (
    <SafeAreaProvider>
      <NavigationContainer theme={theme}>
        <NavigationStack.Navigator initialRouteName="BottomTabs Example">
          <NavigationStack.Screen
            name="BottomTabs Example"
            component={App}
            options={{
              headerRight: () => (
                <Button
                  onPress={() =>
                    Alert.alert(
                      'Alert',
                      `Do you want to change to the ${
                        mode === 'js' ? 'native stack' : 'js stack'
                      } ?`,
                      [
                        { text: 'No', onPress: () => {} },
                        {
                          text: 'Yes',
                          onPress: () => {
                            setMode(mode === 'js' ? 'native' : 'js');
                          },
                        },
                      ]
                    )
                  }
                  title={mode === 'js' ? 'JS' : 'NATIVE'}
                  color="blue"
                />
              ),
            }}
          />
          {examples.map((example, index) => (
            <NavigationStack.Screen
              key={index}
              name={example.name}
              component={example.component}
              options={example.screenOptions}
            />
          ))}
        </NavigationStack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  exampleTouchable: {
    padding: 16,
  },
  exampleText: {
    fontSize: 16,
  },
});
