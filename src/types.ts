import type { ImageSourcePropType } from 'react-native';

export type IconSource = string | ImageSourcePropType;

export type BaseRoute = {
  key: string;
  title?: string;
  badge?: string;
  lazy?: boolean;
  focusedIcon?: ImageSourcePropType;
  unfocusedIcon?: ImageSourcePropType;
};

export type NavigationState<Route extends BaseRoute> = {
  index: number;
  routes: Route[];
};
