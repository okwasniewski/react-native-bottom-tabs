module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'RNCTabView',
        componentDescriptors: ['RNCTabViewComponentDescriptor'],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
      },
    },
  },
};
