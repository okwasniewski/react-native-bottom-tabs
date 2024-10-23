module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'RCTTabView',
        componentDescriptors: ['RCTTabViewComponentDescriptor'],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
      },
    },
  },
};
