# Usage with One

In order to use this navigator with [One](https://onestack.dev/), you need to wrap it with the `withLayoutContext`.


```tsx
import { withLayoutContext } from 'one'
import { createNativeBottomTabNavigator } from 'react-native-bottom-tabs/react-navigation'

const NativeTabsNavigator = createNativeBottomTabNavigator().Navigator

export const NativeTabs = withLayoutContext(
  NativeTabsNavigator
)
```

For props and more information, see the [React Navigation integration guide](/docs/guides/usage-with-react-navigation).


