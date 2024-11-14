const path = require('path');
const pak = require('../../packages/react-native-bottom-tabs/package.json');

module.exports = {
  presets: ['babel-preset-expo'],
  plugins: [
    'react-native-reanimated/plugin',
    [
      'module-resolver',
      {
        extensions: ['.tsx', '.ts', '.js', '.json'],
        alias: {
          'react-native-bottom-tabs': path.join(
            __dirname,
            '../../packages/react-native-bottom-tabs',
            pak.source
          ),
        },
      },
    ],
  ],
};
