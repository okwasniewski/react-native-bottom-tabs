import * as React from 'react';
import { View } from 'react-native';
import type { StyleProp, ViewProps, ViewStyle } from 'react-native';

interface Props extends ViewProps {
  visible: boolean;
  children?: React.ReactNode;
  freezeOnBlur?: boolean;
  style?: StyleProp<ViewStyle>;
  collapsable?: boolean;
}

let Screens: typeof import('react-native-screens') | undefined;

try {
  Screens = require('react-native-screens');
} catch (e) {
  // Ignore
}

export function Screen({ visible, ...rest }: Props) {
  if (Screens?.screensEnabled()) {
    return <Screens.Screen activityState={visible ? 2 : 0} {...rest} />;
  }
  return <View {...rest} />;
}
