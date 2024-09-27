import { StyleSheet } from 'react-native';
import TabView from 'react-native-swiftui-tabview';
import { Article } from './Screens/Article';
import { Contacts } from './Screens/Contacts';
import { Albums } from './Screens/Albums';

const items = {
  home: { title: 'Home', icon: 'house' },
  contacts: { title: 'Contacts', icon: 'person' },
  settings: { title: 'Settings', icon: 'gear' },
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
