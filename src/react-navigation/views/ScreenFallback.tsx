import * as React from 'react';
import { StyleProp, View, ViewStyle } from 'react-native';

type Props = {
  visible: boolean;
  children: React.ReactNode;
  enabled: boolean;
  freezeOnBlur?: boolean;
  style?: StyleProp<ViewStyle>;
  collapsable?: boolean;
};

let Screens: typeof import('react-native-screens') | undefined;

try {
  Screens = require('react-native-screens');
} catch (e) {
  // Ignore
}

export function MaybeScreen({ visible, children, ...rest }: Props) {
  if (Screens?.screensEnabled?.()) {
    return (
      <Screens.Screen activityState={visible ? 2 : 0} {...rest}>
        {children}
      </Screens.Screen>
    );
  }
  return <View {...rest}>{children}</View>;
}
