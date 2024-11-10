# Android Edge to Edge Support

React Native Bottom Tabs supports edge-to-edge navigation on Android.

In order to enable edge-to-edge follow installation instructions for [react-native-edge-to-edge](https://github.com/zoontek/react-native-edge-to-edge).

:::note

When using `react-native-edge-to-edge` config plugin, you don't need `react-native-bottom-tabs` config plugin. Make sure to set `parentTheme` to be either `Material2` or `Material3`.

```json
{
  "expo": {
    "plugins": [
      [
        "react-native-edge-to-edge",
        { "android": { "parentTheme": "Material3" } }
      ]
    ]
  }
}
```

:::


Once this package is installed, this library will automatically use it to enable edge-to-edge navigation on Android.

| Before | After |
|:---:|:---:|
| ![Before](../../public/img/no-edge-to-edge.png) | ![After](../../public/img/edge-to-edge.png) |
