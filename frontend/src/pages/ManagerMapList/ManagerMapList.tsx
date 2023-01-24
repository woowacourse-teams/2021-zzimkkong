import React from 'react';
import Header from 'components/Header/Header';
import TabLayout from 'components/TabLayout/TabLayout';
import { TAB_LABEL, TAB_LIST } from 'constants/tab';

const ManagerMapList = (): JSX.Element => {
  return (
    <>
      <Header />
      <TabLayout tabList={TAB_LIST} defaultTabLabel={TAB_LABEL.MANAGER}>
        ManagerMapList
      </TabLayout>
    </>
  );
};

export default ManagerMapList;
