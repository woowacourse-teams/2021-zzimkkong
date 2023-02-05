import React, { useState } from 'react';
import Switch from 'components/Switch/Switch';
import { MapItem, Space } from 'types/common';
import * as Styled from './Aside.styled';
import ReservationForm from './ReservationForm';

interface Props {
  map: MapItem;
}

const SWITCH_LABEL_LIST = ['예약하기', '예약현황'];

const Aside = ({ map }: Props) => {
  const [selectedLabel, setSelectedLabel] = useState<typeof SWITCH_LABEL_LIST[number]>(
    SWITCH_LABEL_LIST[0]
  );

  return (
    <Styled.Container>
      {map.notice && (
        <Styled.Notice>
          <Styled.NoticeTitle>공지사항</Styled.NoticeTitle>
          <Styled.NoticeText>{map?.notice ?? ''}</Styled.NoticeText>
        </Styled.Notice>
      )}
      <Styled.SwitchContainer>
        <Switch
          labelList={SWITCH_LABEL_LIST}
          selectedLabel={selectedLabel}
          onClick={(label) => setSelectedLabel(label)}
        />
      </Styled.SwitchContainer>

      <ReservationForm map={map} />
    </Styled.Container>
  );
};

export default Aside;
