import React, { ReactNode, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import Layout from 'components/Layout/Layout';
import * as Styled from './TabLayout.styles';

export interface Tab {
  label: string;
  path: string;
}

interface Props {
  children: ReactNode;
  tabList: Tab[];
  defaultTabLabel: Tab['label'];
}

const TabLayout = ({ children, tabList, defaultTabLabel }: Props): JSX.Element => {
  const history = useHistory();

  const handleTabClick = (tab: Tab) => {
    history.push(tab.path);
  };

  useEffect(() => {
    const selectedTab = tabList.find(({ label }) => label === defaultTabLabel) ?? tabList[0];

    history.push(selectedTab.path);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Layout>
      <Styled.Container>
        <Styled.TabItemContainer>
          {tabList.map((tab) => (
            <Styled.TabItem
              onClick={() => handleTabClick(tab)}
              selected={tab.label === defaultTabLabel}
            >
              {tab.label}
            </Styled.TabItem>
          ))}
        </Styled.TabItemContainer>
        {children}
      </Styled.Container>
    </Layout>
  );
};

export default TabLayout;
