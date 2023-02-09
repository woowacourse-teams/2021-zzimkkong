import Switch from 'components/Switch/Switch';
import { MapItem } from 'types/common';
import { SWITCH_LABEL_LIST } from '../GuestMap';
import * as Styled from './Aside.styled';
import ReservationForm from './ReservationForm';

interface Props {
  map: MapItem;
  selectedLabel: typeof SWITCH_LABEL_LIST[number];
  onClickSwitch: (label: typeof SWITCH_LABEL_LIST[number]) => void;
}

const Aside = ({ map, selectedLabel, onClickSwitch }: Props) => {
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

      <ReservationForm map={map} />
    </Styled.Container>
  );
};

export default Aside;
