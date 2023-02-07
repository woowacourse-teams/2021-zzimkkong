import React from 'react';
import * as Styled from './Tab.styles';

export type TabItem = string;

interface Props {
  tabList: TabItem[];
  defaultTabLabel: TabItem;
  onClick: (selectedTab: TabItem) => void;
}

const Tab = ({ tabList, defaultTabLabel, onClick }: Props): JSX.Element => {
  return (
    <Styled.Container>
      {tabList.map((tab) => (
        <Styled.TabItem key={tab} onClick={() => onClick(tab)} selected={tab === defaultTabLabel}>
          {tab}
        </Styled.TabItem>
      ))}
    </Styled.Container>
  );
};

export default Tab;
