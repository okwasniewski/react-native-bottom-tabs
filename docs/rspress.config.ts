import * as path from 'path';
import { defineConfig } from 'rspress/config';
import { pluginOpenGraph } from 'rsbuild-plugin-open-graph';

export default defineConfig({
  root: path.join(__dirname, 'docs'),
  base: '/react-native-bottom-tabs/',
  title: 'React Native Bottom Tabs',
  description: 'React Native Bottom Tabs Documentation',
  logoText: 'React Native Bottom Tabs',
  icon: '/img/phone.png',
  logo: '/img/phone.png',
  themeConfig: {
    socialLinks: [
      {
        icon: 'github',
        mode: 'link',
        content: 'https://github.com/okwasniewski/react-native-bottom-tabs',
      },
    ],
  },
  builderConfig: {
    plugins: [
      pluginOpenGraph({
        title: 'React Native Bottom Tabs',
        type: 'website',
        url: 'https://okwasniewski.github.io/react-native-bottom-tabs/',
        description: 'Native Bottom Tabs for React Native',
      }),
    ],
  },
});
