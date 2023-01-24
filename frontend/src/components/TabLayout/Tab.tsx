import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import * as Styled from './Tab.styles';

export interface TabItem {
  label: string;
  path: string;
}

interface Props {
  tabList: TabItem[];
  defaultTabLabel: TabItem['label'];
}

const Tab = ({ tabList, defaultTabLabel }: Props): JSX.Element => {
  const history = useHistory();

  const handleTabClick = (tab: TabItem) => {
    history.push(tab.path);
  };

  useEffect(() => {
    const selectedTab = tabList.find(({ label }) => label === defaultTabLabel) ?? tabList[0];

    history.push(selectedTab.path);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Styled.Container>
      {tabList.map((tab) => (
        <Styled.TabItem
          key={tab.label}
          onClick={() => handleTabClick(tab)}
          selected={tab.label === defaultTabLabel}
        >
          {tab.label}
        </Styled.TabItem>
      ))}
    </Styled.Container>
  );
};

export default Tab;
