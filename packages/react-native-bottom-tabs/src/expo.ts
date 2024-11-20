import {
  type ConfigPlugin,
  createRunOncePlugin,
  withAndroidStyles,
} from '@expo/config-plugins';

const MATERIAL3_THEME = 'Theme.Material3.DayNight.NoActionBar';
const MATERIAL2_THEME = 'Theme.MaterialComponents.DayNight.NoActionBar';

type ConfigProps = {
  /*
   * Define theme that should be used.
   * @default 'material3'
   */
  theme: 'material2' | 'material3';
};

const withMaterial3Theme: ConfigPlugin<ConfigProps> = (config, options) => {
  const theme = options?.theme;

  return withAndroidStyles(config, (stylesConfig) => {
    stylesConfig.modResults.resources.style =
      stylesConfig.modResults.resources.style?.map((style) => {
        if (style.$.name === 'AppTheme') {
          if (theme === 'material2') {
            style.$.parent = MATERIAL2_THEME;
          } else {
            style.$.parent = MATERIAL3_THEME;
          }
        }

        return style;
      });

    return stylesConfig;
  });
};

export default createRunOncePlugin(
  withMaterial3Theme,
  'react-native-bottom-tabs'
);
