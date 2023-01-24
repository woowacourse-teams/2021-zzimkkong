import React from 'react';
import Header from 'components/Header/Header';
import MemberInfo from 'components/MemberInfo/MemberInfo';
import TabLayout from 'components/TabLayout/TabLayout';
import { TAB_LABEL, TAB_LIST } from 'constants/tab';

const GuestMain = (): JSX.Element => {
  return (
    <>
      <Header />
      <TabLayout tabList={TAB_LIST} defaultTabLabel={TAB_LABEL.GUEST}>
        <MemberInfo />
      </TabLayout>
    </>
  );
};

export default GuestMain;
