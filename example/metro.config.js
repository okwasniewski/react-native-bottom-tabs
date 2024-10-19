const path = require('path');
const { makeMetroConfig } = require('@rnx-kit/metro-config');
const root = path.resolve(__dirname, '..');
const pack = require('../package.json');
const modules = Object.keys(pack.peerDependencies);

module.exports = makeMetroConfig({
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: false,
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
  watchFolders: [root],
});
