import React from 'react';
import TabLayout from 'components/TabLayout/TabLayout';
import { TAB_LABEL, TAB_LIST } from 'constants/tab';

const GuestMain = (): JSX.Element => {
  return (
    <TabLayout tabList={TAB_LIST} defaultTabLabel={TAB_LABEL.GUEST}>
      GuestMain
    </TabLayout>
  );
};

export default GuestMain;
