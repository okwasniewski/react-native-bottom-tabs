<h1 align="center">
  React Native Bottom Tabs
</h1>

<p align="center">
  <strong>Native Bottom tabs</strong><br>
  using SwiftUI's TabView and BottomNavigationView for React Native.
</p>

## 📦 Installation

```sh
npm install react-native-bottom-tabs
```

```sh
yarn add react-native-bottom-tabs
```

## 📖 Documentation


```tsx
import TabView, { SceneMap } from 'react-native-bottom-tabs';

export default function ThreeTabs() {
  const [index, setIndex] = useState(0);
  const [routes] = useState([
    { key: 'article', title: 'Article', icon: 'document.fill', badge: '!' },
    {
      key: 'albums',
      title: 'Albums',
      icon: 'square.grid.2x2.fill',
      badge: '5',
    },
    { key: 'contacts', title: 'Contacts', icon: 'person.fill' },
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

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
