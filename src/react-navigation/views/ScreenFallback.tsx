import * as React from 'react';
import { Platform, StyleProp, View, ViewProps, ViewStyle } from 'react-native';

type Props = {
  visible: boolean;
  children?: React.ReactNode;
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

export const MaybeScreenContainer = ({ children, ...rest }: ViewProps) => {
  if (Platform.OS === 'android' && Screens?.screensEnabled()) {
    return (
      <Screens.ScreenContainer {...rest}>{children}</Screens.ScreenContainer>
    );
  }

  return <>{children}</>;
};

export function MaybeScreen({ visible, ...rest }: Props) {
  if (Screens?.screensEnabled()) {
    return <Screens.Screen activityState={visible ? 2 : 0} {...rest} />;
  }
  return <View {...rest} />;
}
