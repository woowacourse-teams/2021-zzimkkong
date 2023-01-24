import { AxiosError } from 'axios';
import React, { ComponentProps, useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { postNotice } from 'api/managerMap';
import { ReactComponent as NoticeIcon } from 'assets/svg/notice.svg';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import Modal from 'components/Modal/Modal';
import TextArea from 'components/TextArea/TextArea';
import MESSAGE from 'constants/message';
import useInput from 'hooks/useInput';
import { ErrorResponse, QueryManagerMapSuccess } from 'types/response';
import * as Styled from './ManagerIconButton.styled';

interface Props extends ComponentProps<typeof IconButton> {
  map: QueryManagerMapSuccess;
}

const MapNoticeButton = ({ map, ...props }: Props): JSX.Element => {
  const [isModalOpened, setIsModalOpened] = useState(false);
  const [noticeText, onChangeNoticeText, setNoticeText] = useInput();

  const handleModalClose = () => {
    setIsModalOpened(false);
  };

  const setMapNotice = useMutation(postNotice, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.NOTICE_SET_SUCCESS);

      setIsModalOpened(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_NOTICE_SET_ERROR);
    },
  });

  const handleSubmitNotice = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setMapNotice.mutate({
      mapId: map.mapId,
      notice: noticeText,
    });
  };

  useEffect(() => {
    setNoticeText(map.notice ?? '');
  }, [setNoticeText, map]);

  return (
    <>
      <IconButton {...props} onClick={() => setIsModalOpened(true)}>
        <NoticeIcon width="100%" height="100%" />
      </IconButton>

      {isModalOpened && (
        <Modal open={isModalOpened} isClosableDimmer={true} onClose={handleModalClose}>
          <Modal.Header>공지사항을 입력해주세요</Modal.Header>
          <Modal.Inner>
            <form onSubmit={handleSubmitNotice}>
              <TextArea
                label="공지사항"
                rows={4}
                maxLength={100}
                value={noticeText}
                onChange={onChangeNoticeText}
                autoFocus
              />
              <Styled.ModalFooter>
                <Button variant="text" type="button" onClick={handleModalClose}>
                  취소
                </Button>
                <Button variant="text">확인</Button>
              </Styled.ModalFooter>
            </form>
          </Modal.Inner>
        </Modal>
      )}
    </>
  );
};

export default MapNoticeButton;
