import { StyleSheet } from 'react-native';
import TabView from 'react-native-swiftui-tabview';
import { Article } from './Screens/Article';
import { Contacts } from './Screens/Contacts';
import { Albums } from './Screens/Albums';

const items = {
  home: { title: 'Article', icon: 'document.fill' },
  contacts: { title: 'Albums', icon: 'square.grid.2x2.fill' },
  settings: { title: 'Contacts', icon: 'person.fill' },
};

export default function App() {
  return (
    <TabView style={styles.fullWidth} items={items}>
      <Article />
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
