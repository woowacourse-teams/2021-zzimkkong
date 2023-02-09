import Switch from 'components/Switch/Switch';
import { MapItem } from 'types/common';
import { SWITCH_LABEL_LIST } from '../GuestMap';
import * as Styled from './Aside.styled';
import ReservationForm from './ReservationForm';
import ReservationList from './ReservationList';

interface Props {
  map: MapItem;
  selectedLabel: typeof SWITCH_LABEL_LIST[number];
  onClickSwitch: (label: typeof SWITCH_LABEL_LIST[number]) => void;
}

const Aside = ({ map, selectedLabel, onClickSwitch }: Props) => {
  const isReservation = selectedLabel === SWITCH_LABEL_LIST[0];

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
          onClick={onClickSwitch}
        />
      </Styled.SwitchContainer>

      {isReservation ? <ReservationForm map={map} /> : <ReservationList map={map} />}
    </Styled.Container>
  );
};

export default Aside;
