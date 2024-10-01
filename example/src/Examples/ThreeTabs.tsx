import { StyleSheet } from 'react-native';
import TabView, {
  type OnPageSelectedEventData,
  type TabViewItems,
} from 'react-native-bottom-tabs';
import { useState } from 'react';
import { Article } from '../Screens/Article';
import { Albums } from '../Screens/Albums';
import { Contacts } from '../Screens/Contacts';

const items: TabViewItems = [
  { key: 'article', title: 'Article', icon: 'document.fill', badge: '!' },
  { key: 'albums', title: 'Albums', icon: 'square.grid.2x2.fill', badge: '5' },
  { key: 'contacts', title: 'Contacts', icon: 'person.fill' },
];

export default function ThreeTabs() {
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
    </TabView>
  );
}

const styles = StyleSheet.create({
  fullWidth: {
    width: '100%',
    height: '100%',
  },
});
