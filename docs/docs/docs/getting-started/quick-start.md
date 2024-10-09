# Quick Start

## Installation

```bash
yarn add react-native-bottom-tabs
```

If you use React Native version 0.75 or lower:

- For `@react-native-community/cli` users, open Podfile in ios folder and change minimum iOS version to `14.0` before `pod install`

```diff
- platform :ios, min_ios_version_supported
+ platform :ios, '14.0'
```

- For Expo users, install `expo-build-properties`, open app.json file and update `deploymentTarget` for `ios` as below

```json
{
  "expo": {
    "plugins": [
      [
        "expo-build-properties",
        {
          "ios": {
            "deploymentTarget": "14.0"
          }
        }
      ]
    ],
  }
}
```

## Example usage

:::warning
To use this navigator, ensure that you have [`@react-navigation/native` and its dependencies (follow this guide)](https://reactnavigation.org/docs/getting-started).
:::

```tsx
import {createNativeBottomTabNavigator} from 'react-native-bottom-tabs/react-navigation';

const Tabs = createNativeBottomTabNavigator();

function NativeBottomTabs() {
  return (
    <Tabs.Navigator>
      <Tabs.Screen
        name="index"
        options={{
          title: "Home",
          tabBarIcon: () => ({ sfSymbol: "house" }),
        }}
      />
      <Tabs.Screen
        name="explore"
        options={{
          title: "Explore",
          tabBarIcon: () => ({ sfSymbol: "person" }),
        }}
      />
    </Tabs.Navigator>
  );
}
```
