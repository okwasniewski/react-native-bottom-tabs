# Standalone usage

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
