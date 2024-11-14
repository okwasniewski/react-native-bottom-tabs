const path = require('path');
const pak = require('../../packages/react-native-bottom-tabs/package.json');

module.exports = {
  presets: ['module:@react-native/babel-preset'],
  plugins: [
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
