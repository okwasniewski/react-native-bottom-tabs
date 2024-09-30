import { StyleSheet } from 'react-native';
import TabView, {
  type OnPageSelectedEventData,
  type TabViewItems,
} from 'react-native-bottom-tabs';
import { Article } from './Screens/Article';
import { Contacts } from './Screens/Contacts';
import { Albums } from './Screens/Albums';
import { useState } from 'react';
import { Chat } from './Screens/Chat';

const items: TabViewItems = [
  { key: 'article', title: 'Article', icon: 'document.fill', badge: '!' },
  { key: 'albums', title: 'Albums', icon: 'square.grid.2x2.fill', badge: '5' },
  { key: 'contacts', title: 'Contacts', icon: 'person.fill' },
  { key: 'chat', title: 'Chat', icon: 'keyboard' },
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
      tabViewStyle="sidebarAdaptable"
      selectedPage={selectedPage}
      onPageSelected={handlePageSelected}
    >
      <Article onClick={goToAlbums} />
      <Albums />
      <Contacts />
      <Chat />
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
