import React, { ReactNode } from 'react';
import Layout from 'components/Layout/Layout';
import Tab, { TabItem } from './Tab';
import * as Styled from './TabLayout.styles';

interface Props {
  children: ReactNode;
  tabList: TabItem[];
  defaultTabLabel: TabItem;
  onClick: (selectedTab: TabItem) => void;
}

const TabLayout = ({ children, tabList, defaultTabLabel, onClick }: Props): JSX.Element => {
  return (
    <Styled.Container>
      <Layout>
        <Styled.TabContainer>
          <Tab tabList={tabList} defaultTabLabel={defaultTabLabel} onClick={onClick} />
          <Styled.Content>{children}</Styled.Content>
        </Styled.TabContainer>
      </Layout>
    </Styled.Container>
  );
};

export default TabLayout;
