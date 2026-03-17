import { useState } from 'react';
import Input from 'components/Input/Input';
import Modal from 'components/Modal/Modal';
import QRCode from 'components/QRCode/QRCode';
import useSharingSpaceId from 'hooks/query/useSharingSpaceId';

interface QRCodeModalProps {
  open: boolean;
  onClose: () => void;
  mapId: number;
  spaceId: number;
  spaceName: string;
}

const QRCodeModal = ({
  open,
  onClose,
  mapId,
  spaceId,
  spaceName,
}: QRCodeModalProps): JSX.Element => {
  const [description, setDescription] = useState('');

  const getSharingSpaceId = useSharingSpaceId(
    { mapId, spaceId },
    {
      enabled: open,
      retry: false,
    }
  );

  const sharingSpaceId = getSharingSpaceId.data?.data?.sharingSpaceId;

  return (
    <Modal open={open} onClose={onClose} isClosableDimmer showCloseButton>
      <Modal.Inner>
        <Modal.Header>QR 코드 - {spaceName}</Modal.Header>
        <Modal.Content>
          {getSharingSpaceId.isLoading && <p>QR 코드를 생성하는 중...</p>}
          {getSharingSpaceId.isError && <p>QR 코드를 생성하는 데 실패했습니다.</p>}
          {sharingSpaceId && (
            <>
              <div style={{ padding: '0 1rem 1rem' }}>
                <Input
                  label="기본 사용 목적 (선택)"
                  name="description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="예: 코치 회의"
                />
              </div>
              <QRCode
                sharingSpaceId={sharingSpaceId}
                spaceName={spaceName}
                description={description}
              />
            </>
          )}
        </Modal.Content>
      </Modal.Inner>
    </Modal>
  );
};

export default QRCodeModal;
