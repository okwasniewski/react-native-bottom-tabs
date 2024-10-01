import * as React from 'react';

type SceneProps = {
  route: any;
  jumpTo: (key: string) => void;
};

const SceneComponent = React.memo(
  <T extends { component: React.ComponentType<any> } & SceneProps>({
    component,
    ...rest
  }: T) => {
    return React.createElement(component, rest);
  }
);

SceneComponent.displayName = 'SceneComponent';

export function SceneMap<T>(scenes: { [key: string]: React.ComponentType<T> }) {
  return ({ route, jumpTo }: SceneProps) => {
    const component = scenes[route.key];
    if (!component) {
      return null;
    }
    return (
      <SceneComponent
        key={route.key}
        jumpTo={jumpTo}
        component={component}
        route={route}
      />
    );
  };
}
