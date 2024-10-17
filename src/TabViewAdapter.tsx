import { processColor } from 'react-native';
import NativeTabView, { TabViewNativeProps } from './TabViewNativeComponent';

const TabViewAdapter = ({
  activeTintColor,
  inactiveTintColor,
  ...props
}: TabViewNativeProps) => {
  return (
    <NativeTabView
      {...props}
      activeTintColor={processColor(activeTintColor)}
      inactiveTintColor={processColor(inactiveTintColor)}
    />
  );
};

export default TabViewAdapter;
