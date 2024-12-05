const { makeMetroConfig } = require('@rnx-kit/metro-config');
const path = require('path');

const root = path.resolve(__dirname, '../..');
const pack = require('../../packages/react-native-bottom-tabs/package.json');
const modules = Object.keys(pack.peerDependencies);

const extraConfig = {
  watchFolders: [root],
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  resolver: {
    unstable_enableSymlinks: true,
    extraNodeModules: modules.reduce((acc, name) => {
      acc[name] = path.join(__dirname, 'node_modules', name);
      return acc;
    }, {}),
  },
};

const metroConfig = makeMetroConfig(extraConfig);

module.exports = metroConfig;
