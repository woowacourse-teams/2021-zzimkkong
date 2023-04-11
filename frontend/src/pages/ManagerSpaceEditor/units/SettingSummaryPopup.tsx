import React from 'react';
import Modal from '../../../components/Modal/Modal';
import SummaryModal from '../../../components/Modal/SummaryModal';
import useSettingSummary from '../../../hooks/query/useSettingSummary';
import * as Styled from './SettingSummaryPopup.styles';

interface SettingSummaryProps {
  mapId: number;
  spaceId: number;
  open: boolean;
  onClose: () => void;
}

const SettingSummaryPopup = ({
  mapId,
  spaceId,
  open,
  onClose,
}: SettingSummaryProps): JSX.Element => {
  const getSettingsSummary = useSettingSummary(
    {
      mapId,
      spaceId,
      selectedDateTime: null,
      settingViewType: 'FLAT',
    },
    {}
  );
  const settingsSummary = getSettingsSummary.data?.data?.summary ?? '';

  return (
    <SummaryModal open={open} onClose={onClose} showCloseButton={true} isClosableDimmer={true}>
      <Styled.SettingSummaryPopupWrapper>
        <Styled.SettingSummary fontWeight="bold">{settingsSummary}</Styled.SettingSummary>
      </Styled.SettingSummaryPopupWrapper>
    </SummaryModal>
  );
};

export default SettingSummaryPopup;
