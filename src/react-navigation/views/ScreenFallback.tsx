import * as React from 'react';
import { Platform, StyleProp, View, ViewProps, ViewStyle } from 'react-native';

type Props = {
  visible: boolean;
  children?: React.ReactNode;
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

export const MaybeScreenContainer = ({
  enabled,
  children,
  ...rest
}: ViewProps & {
  enabled: boolean;
  hasTwoStates: boolean;
  children?: React.ReactNode;
}) => {
  if (Platform.OS === 'android' && Screens?.screensEnabled()) {
    return (
      <Screens.ScreenContainer enabled={enabled} {...rest}>
        {children}
      </Screens.ScreenContainer>
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
