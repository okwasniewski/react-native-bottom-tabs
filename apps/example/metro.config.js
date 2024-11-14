const { makeMetroConfig } = require("@rnx-kit/metro-config");
const path = require('path');

const root = path.resolve(__dirname, '../..');

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
};

const metroConfig = makeMetroConfig(extraConfig);

module.exports = metroConfig;
