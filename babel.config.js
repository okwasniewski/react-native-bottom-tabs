module.exports = {
  presets: [
    'module:@react-native/babel-preset',
    ['module:react-native-builder-bob/babel-preset', { modules: 'commonjs' }],
  ],
};
