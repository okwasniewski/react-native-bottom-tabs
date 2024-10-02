import TabView, { SceneMap } from 'react-native-bottom-tabs';
import { useState } from 'react';
import { Article } from '../Screens/Article';
import { Albums } from '../Screens/Albums';
import { Contacts } from '../Screens/Contacts';
import { Chat } from '../Screens/Chat';

export default function FourTabs() {
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
    { key: 'chat', title: 'Chat', icon: 'keyboard' },
  ]);

  const renderScene = SceneMap({
    article: Article,
    albums: Albums,
    contacts: Contacts,
    chat: Chat,
  });

  return (
    <TabView
      navigationState={{ index, routes }}
      onIndexChange={setIndex}
      renderScene={renderScene}
    />
  );
}
