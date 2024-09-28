import { StyleSheet } from 'react-native';
import TabView, {
  type OnPageSelectedEventData,
  type TabViewItems,
} from 'react-native-swiftui-tabview';
import { Article } from './Screens/Article';
import { Contacts } from './Screens/Contacts';
import { Albums } from './Screens/Albums';
import { useState } from 'react';

const items: TabViewItems = [
  { key: 'article', title: 'Article', icon: 'document.fill' },
  { key: 'albums', title: 'Albums', icon: 'square.grid.2x2.fill' },
  { key: 'contacts', title: 'Contacts', icon: 'person.fill', badge: '3' },
];

export default function App() {
  const [selectedPage, setSelectedTab] = useState<string>('contacts');

  const handlePageSelected = ({
    nativeEvent: { key },
  }: {
    nativeEvent: OnPageSelectedEventData;
  }) => setSelectedTab(key);

  const goToAlbums = () => {
    setSelectedTab('albums');
  };

  return (
    <TabView
      style={styles.fullWidth}
      items={items}
      selectedPage={selectedPage}
      onPageSelected={handlePageSelected}
    >
      <Article onClick={goToAlbums} />
      <Albums />
      <Contacts />
    </TabView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  fullWidth: {
    width: '100%',
    height: '100%',
  },
});
