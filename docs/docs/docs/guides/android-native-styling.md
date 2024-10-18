# Android Native Styling

## Expo users

TODO: Add instructions for Expo users

## React Native Community CLI users

Inside of your `android/app/src/main/res/values/styles.xml` file you can customize the appearance of the native bottom tabs.

Here is how the file looks by default:

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="android:editTextBackground">@drawable/rn_edit_text_material</item>
    </style>
</resources>
```

In order to use the native bottom tabs, you need to change `AppTheme` to extend from `Theme.MaterialComponents.DayNight.NoActionBar`:

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="android:editTextBackground">@drawable/rn_edit_text_material</item>
    </style>
</resources>
```

:::warning
This is required for the native bottom tabs to work correctly.

You might see this error if you don't change the theme:

`The style on this component requires your app theme to be Theme.MaterialComponents (or a descendant).`

:::

If you want to use Material Design 3, you can extend from `Theme.Material3.DayNight.NoActionBar`:

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="android:editTextBackground">@drawable/rn_edit_text_material</item>
    </style>
</resources>
```
