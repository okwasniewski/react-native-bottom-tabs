# Android Native Styling

## Expo users

Use Expo Config Plugin for Material 3 styling:

```diff
  "expo": {
+   "plugins": ["react-native-bottom-tabs"]
  }
}
```

If you want to use Material2 styling, you can pass `theme` option to the plugin:


```diff
  "expo": {
+   "plugins": [["react-native-bottom-tabs", { "theme": "material2" }]]
  }
}
```

## React Native Community CLI users

Inside of your `android/app/src/main/res/values/styles.xml` file you can customize the appearance of the native bottom tabs.


```diff
<resources>
- <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
+ <style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- … -->
  </style>
</resources>
```

If you want to use Material Design 2, you can extend from `Theme.MaterialComponents.DayNight.NoActionBar`:

```diff
<resources>
- <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
+ <style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <!-- … -->
  </style>
</resources>
```

