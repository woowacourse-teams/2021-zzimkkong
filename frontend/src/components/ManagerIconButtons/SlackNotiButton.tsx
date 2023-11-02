import { AxiosError } from 'axios';
import React, { ComponentProps, useState } from 'react';
import { useMutation } from 'react-query';
import { postSlackWebhookUrl } from 'api/managerMap';
import { ReactComponent as SlackIcon } from 'assets/svg/slack.svg';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';
import Modal from 'components/Modal/Modal';
import MESSAGE from 'constants/message';
import useInput from 'hooks/useInput';
import useSlackWebhookUrl from 'pages/ManagerMapDetail/hooks/useSlackWebhookUrl';
import { ErrorResponse } from 'types/response';
import { QueryManagerMapSuccessV2 } from 'types/response-v2';
import * as Styled from './ManagerIconButton.styled';

interface Props extends ComponentProps<typeof IconButton> {
  map: QueryManagerMapSuccessV2;
}

// TODO: 슬랙 API 개발시 이부분 API 요청 변경해야함.
const SlackNotiButton = ({ map, ...props }: Props): JSX.Element => {
  const [isModalOpened, setIsModalOpened] = useState(false);
  const [slackUrl, onChangeSlackUrl, setSlackUrl] = useInput();

  const handleModalClose = () => {
    setIsModalOpened(false);
  };

  const getSlackWebhookUrl = useSlackWebhookUrl(
    { mapId: map.mapId },
    {
      refetchOnWindowFocus: false,
      onSuccess: (response) => {
        if (!slackUrl) setSlackUrl(response.data.slackUrl);
      },
    }
  );

  const createSlackWebhookUrl = useMutation(postSlackWebhookUrl, {
    onSuccess: () => {
      getSlackWebhookUrl.refetch();
      alert(MESSAGE.MANAGER_MAIN.SLACK_WEBHOOK_CREATE_SUCCESS);

      setIsModalOpened(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(
        error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_SLACK_WEBHOOK_CREATE_ERROR
      );
    },
  });

  const handleSubmitSlackUrl = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    createSlackWebhookUrl.mutate({
      mapId: map.mapId,
      slackUrl,
    });
  };

  return (
    <>
      <IconButton {...props} onClick={() => setIsModalOpened(true)}>
        <SlackIcon width="100%" height="100%" />
      </IconButton>

      {isModalOpened && (
        <Modal open={isModalOpened} isClosableDimmer={true} onClose={handleModalClose}>
          <Modal.Header>알림을 받을 슬랙 웹훅 URL을 입력해주세요</Modal.Header>
          <Modal.Inner>
            <form onSubmit={handleSubmitSlackUrl}>
              <Input
                type="text"
                label="웹훅 URL"
                value={slackUrl}
                onChange={onChangeSlackUrl}
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

export default SlackNotiButton;
