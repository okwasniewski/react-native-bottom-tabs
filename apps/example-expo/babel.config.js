const path = require('path');
const tabView = require('../../packages/react-native-bottom-tabs/package.json');
const reactNavigationIntegration = require('../../packages/react-navigation/package.json');

module.exports = {
  presets: ['babel-preset-expo'],
  plugins: [
    [
      'module-resolver',
      {
        extensions: ['.tsx', '.ts', '.js', '.json'],
        alias: {
          'react-native-bottom-tabs': path.join(
            __dirname,
            '../../packages/react-native-bottom-tabs',
            tabView.source
          ),
          '@bottom-tabs/react-navigation': path.join(
            __dirname,
            '../../packages/react-navigation',
            reactNavigationIntegration.source
          ),
        },
      },
    ],
  ],
};
