module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'RNCTabViewSpec',
        componentDescriptors: ['RCTTabViewComponentDescriptor'],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
      },
    },
  },
};
