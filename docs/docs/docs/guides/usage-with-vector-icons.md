# Usage with React Native Vector Icons

This library supports using [React Native Vector Icons](https://github.com/oblador/react-native-vector-icons) thanks to the `getImageSourceSync` API.

:::info

Follow installation guide in React Native Vector Icons [README](https://github.com/oblador/react-native-vector-icons)

:::

### Usage

```tsx
import { createNativeBottomTabNavigator } from 'react-native-bottom-tabs/react-navigation';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';

const homeIcon = Icon.getImageSourceSync('home', 24);
const exploreIcon = Icon.getImageSourceSync('compass', 24);

const Tabs = createNativeBottomTabNavigator();

function NativeBottomTabs() {
  return (
    <Tabs.Navigator ignoresTopSafeArea>
        <Tabs.Screen
          name="Home"
          component={HomeScreen}
          options={{
            tabBarIcon: () => homeIcon,
          }}
        />
        <Tabs.Screen
          name="Explore"
          component={ExploreScreen}
          options={{
            tabBarIcon: () => exploreIcon,
          }}
        />
    </Tabs.Navigator>
  );
}
```

