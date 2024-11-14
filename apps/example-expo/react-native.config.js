const path = require('path');
const pak = require('../../packages/react-native-bottom-tabs/package.json');

module.exports = {
  project: {
    ios: {
      automaticPodsInstallation: true,
    },
  },
  dependencies: {
    [pak.name]: {
      root: path.join(
        __dirname,
        '..',
        '..',
        'packages',
        'react-native-bottom-tabs'
      ),
    },
  },
};
