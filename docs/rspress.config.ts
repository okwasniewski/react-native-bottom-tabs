import * as path from 'path';
import { defineConfig } from 'rspress/config';

export default defineConfig({
  root: path.join(__dirname, 'docs'),
  base: '/react-native-bottom-tabs/',
  title: 'Native Bottom Tabs',
  description: 'React Native Bottom Tabs Documentation',
  themeConfig: {
    socialLinks: [
      {
        icon: 'github',
        mode: 'link',
        content: 'https://github.com/okwasniewski/react-native-bottom-tabs',
      },
    ],
  },
});
