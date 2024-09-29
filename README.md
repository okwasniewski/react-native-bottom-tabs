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
import { TabView } from "react-native-bottom-tabs";

const items: TabViewItems = [
  { key: 'article', title: 'Article', icon: 'document.fill' },
  { key: 'albums', title: 'Albums', icon: 'square.grid.2x2.fill', badge: '3' },
  { key: 'contacts', title: 'Contacts', icon: 'person.fill' },
];

export default function App() {
  const [selectedPage, setSelectedTab] = useState<string>('contacts');

  return (
    <TabView
      style={styles.fullWidth}
      items={items}
      selectedPage={selectedPage}
      onPageSelected={handlePageSelected}
    >
      <View/>
      <View/>
      <View/>
    </TabView>
  );
}
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
