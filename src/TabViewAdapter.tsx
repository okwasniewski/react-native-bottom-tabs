import NativeTabView, { type TabViewProps } from './TabViewNativeComponent';

const TabViewAdapter = ({ ...props }: TabViewProps) => {
  return <NativeTabView {...props} />;
};

export default TabViewAdapter;
