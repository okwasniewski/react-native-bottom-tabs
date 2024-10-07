<h1 align="center">
  React Native Bottom Tabs
</h1>

<p align="center">
  <strong>Bottom Tabs that use native platform primitives.</strong><br>
</p>

> [!CAUTION]
> This library is in early development and not ready for production use.

https://github.com/user-attachments/assets/fbdd9ce2-f4b9-4d0c-bd91-2e62bb422d69

## 📦 Installation

```sh
yarn add react-native-bottom-tabs
```

## 📖 Documentation

### Usage with React Navigation


> [!NOTE]
> To use this navigator, ensure that you have [`@react-navigation/native` and its dependencies (follow this guide)](https://reactnavigation.org/docs/getting-started):

Example usage:

```tsx
import {createNativeBottomTabNavigator} from 'react-native-bottom-tabs/react-navigation';

const Tab = createNativeBottomTabNavigator();

function NativeBottomTabs() {
  return (
    <Tab.Navigator>
      <Tab.Screen
        name="Article"
        component={Article}
        options={{
          tabBarBadge: '10',
          tabBarIcon: ({ focused }) =>
            focused
              ? require('person.png')
              : require('article.png'),
        }}
      />
      <Tab.Screen
        name="Albums"
        component={Albums}
        options={{
          tabBarIcon: () => require('grid.png'),
        }}
      />
    </Tab.Navigator>
  );
}
```

### Props

The `Tab.Navigator` component accepts following props:

#### `id`

Optional unique ID for the navigator. This can be used with `navigation.getParent` to refer to this navigator in a child navigator.

#### `initialRouteName`

The name of the route to render on first load of the navigator.

#### `screenOptions`

Default options to use for the screens in the navigator.

#### `labeled`

Whether to show labels in tabs. Defaults to true.

#### `sidebarAdaptable`

A tab bar style that adapts to each platform. (Apple platforms only)

Tab views using the sidebar adaptable style have an appearance
- iPadOS displays a top tab bar that can adapt into a sidebar.
- iOS displays a bottom tab bar.
- macOS and tvOS always show a sidebar.
- visionOS shows an ornament and also shows a sidebar for secondary tabs within a `TabSection`.


### Options

The following options can be used to configure the screens in the navigator. These can be specified under `screenOptions` prop of `Tab.navigator` or `options` prop of `Tab.Screen`.

#### `title`

Title text for the screen.

#### `tabBarLabel`

Label text of the tab displayed in the navigation bar. When undefined, scene title is used.

#### `tabBarIcon`

Function that given { focused: boolean } returns ImageSource or AppleIcon to display in the navigation bar.

Note: SF Symbols are only supported on Apple platforms.

```tsx
<Tab.Screen
  name="Albums"
  component={Albums}
  options={{
    tabBarIcon: () => require('person.png'),
    // or
    tabBarIcon: () => ({ sfSymbol: 'person' }),
  }}
/>

```

#### `tabBarBadge`

Badge to show on the tab icon.

#### `lazy`

Whether this screens should render the first time it's accessed. Defaults to true. Set it to false if you want to render the screen on initial render.


### Usage without React Navigation

If you don't use React Navigation, you can use the `TabView` component directly.


```tsx
import TabView, { SceneMap } from 'react-native-bottom-tabs';

export default function ThreeTabs() {
  const [index, setIndex] = useState(0);
  const [routes] = useState([
    { key: 'article', title: 'Article', focusedIcon: require('article.png'), badge: '!' },
    {
      key: 'albums',
      title: 'Albums',
      focusedIcon: require('grid.png'),
      badge: '5',
    },
    { key: 'contacts', title: 'Contacts', focusedIcon: require('person.png') },
  ]);

  const renderScene = SceneMap({
    article: Article,
    albums: Albums,
    contacts: Contacts,
  });

  return (
    <TabView
      navigationState={{ index, routes }}
      onIndexChange={setIndex}
      renderScene={renderScene}
    />
  );
}
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with ❤️ and [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
