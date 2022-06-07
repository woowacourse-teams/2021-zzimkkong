import { AxiosError } from 'axios';
import dayjs, { Dayjs } from 'dayjs';
import React, { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteMap, postNotice, postSlackWebhookUrl } from 'api/managerMap';
import { deleteManagerReservation } from 'api/managerReservation';
import { ReactComponent as MapEditorIcon } from 'assets/svg/map-editor.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as NoticeIcon } from 'assets/svg/notice.svg';
import { ReactComponent as SlackIcon } from 'assets/svg/slack.svg';
import { ReactComponent as SpaceEditorIcon } from 'assets/svg/space-editor.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import PageHeader from 'components/PageHeader/PageHeader';
import TextArea from 'components/TextArea/TextArea';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMapReservations from 'hooks/query/useManagerMapReservations';
import useManagerMaps from 'hooks/query/useManagerMaps';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import useInput from 'hooks/useInput';
import { Reservation } from 'types/common';
import { ErrorResponse, MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ManagerMain.styles';
import useSlackWebhookUrl from './hooks/useSlackWebhookUrl';
import MapDrawer from './units/MapDrawer';
import ReservationList from './units/ReservationList';

export interface ManagerMainState {
  mapId?: number;
  targetDate?: Dayjs;
}

type OpenableModal = 'slack' | 'notice';

const ManagerMain = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<ManagerMainState>();

  const mapId = location.state?.mapId;
  const targetDate = location.state?.targetDate;

  const [date, setDate] = useState(targetDate ?? dayjs().tz());
  const [mapDrawerOpen, setMapDrawerOpen] = useState(false);
  const [openedModal, setOpenedModal] = useState<OpenableModal | null>(null);

  const [selectedMapId, setSelectedMapId] = useState<number | null>(mapId ?? null);
  const [selectedMapName, setSelectedMapName] = useState('');

  const [slackUrl, onChangeSlackUrl, setSlackUrl] = useInput();
  const [noticeText, onChangeNoticeText, setNoticeText] = useInput();

  const onRequestError = (error: AxiosError<ErrorResponse>) => {
    alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
  };

  const getMaps = useManagerMaps({
    onError: onRequestError,
  });

  const organization = getMaps.data?.data.organization ?? '';
  const maps = useMemo((): MapItemResponse[] => getMaps.data?.data.maps ?? [], [getMaps]);

  const getSpaces = useManagerSpaces(
    {
      mapId: selectedMapId as number,
    },
    {
      enabled: !isNullish(selectedMapId),
    }
  );

  const getReservations = useManagerMapReservations(
    {
      mapId: selectedMapId as number,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(selectedMapId),
      onError: (error: AxiosError<ErrorResponse>) => {
        alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
      },
    }
  );
  const reservations = useMemo(() => getReservations.data?.data?.data ?? [], [getReservations]);

  const removeReservation = useMutation(deleteManagerReservation, {
    onSuccess: () => {
      getReservations.refetch();
      alert(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const spaces = useMemo(() => getSpaces.data?.data.spaces ?? [], [getSpaces]);

  const removeMap = useMutation(deleteMap, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.MAP_DELETED);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_MAP_DELETE_ERROR);
    },
  });

  const getSlackWebhookUrl = useSlackWebhookUrl(
    { mapId: selectedMapId as number },
    {
      enabled: !!selectedMapId,
      refetchOnWindowFocus: false,
      onSuccess: (response) => {
        if (!slackUrl) setSlackUrl(response.data.slackUrl);
      },
    }
  );

  const createSlackWebhookUrl = useMutation(postSlackWebhookUrl, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.SLACK_WEBHOOK_CREATE_SUCCESS);
      getSlackWebhookUrl.refetch();
      setOpenedModal('slack');
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(
        error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_SLACK_WEBHOOK_CREATE_ERROR
      );
    },
  });

  const setMapNotice = useMutation(postNotice, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.NOTICE_SET_SUCCESS);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_NOTICE_SET_ERROR);
    },
  });

  const handleDeleteMap = (mapId: number) => {
    if (window.confirm(MESSAGE.MANAGER_MAIN.MAP_DELETE_CONFIRM)) {
      removeMap.mutate({ mapId });
    }
  };

  const getSelectedSharingMapId = () => {
    const selectedMap = maps.find((map) => map.mapId === selectedMapId);

    return selectedMap?.sharingMapId ?? '';
  };

  const handleClickMapEditorIcon = () => {
    if (selectedMapId === null) return;

    history.push(HREF.MANAGER_MAP_EDIT(selectedMapId));
  };

  const handleClickSpaceEditorIcon = () => {
    if (selectedMapId === null) return;

    history.push(HREF.MANAGER_SPACE_EDIT(selectedMapId));
  };

  const handleCopyLink = () => {
    if (selectedMapId === null) {
      alert(MESSAGE.MANAGER_MAIN.SELECT_MAP);

      return;
    }

    navigator.clipboard
      .writeText(`${window.location.origin}/guest/${getSelectedSharingMapId()}`)
      .then(() => {
        alert(MESSAGE.MANAGER_MAIN.COPIED_SHARE_LINK);
      })
      .catch(() => {
        alert(MESSAGE.MANAGER_MAIN.UNEXPECTED_COPY_SHARE_LINK);
      });
  };

  const handleOpenDrawer = () => {
    setMapDrawerOpen(true);
  };

  const handleCloseDrawer = () => {
    setMapDrawerOpen(false);
  };

  const closeModal = () => {
    setOpenedModal(null);
  };

  const handleSelectMap = (mapId: number, mapName: string) => {
    setSelectedMapId(mapId);
    setSelectedMapName(mapName);
    handleCloseDrawer();
  };

  const handleCreateReservation = (spaceId: number) => {
    if (selectedMapId === null) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION,
      state: {
        mapId: selectedMapId,
        space: spaces?.find(({ id }) => id === spaceId),
        selectedDate: formatDate(date),
      },
    });
  };

  const handleEditReservation = (reservation: Reservation, spaceId: number) => {
    if (selectedMapId === null) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION_EDIT,
      state: {
        mapId: selectedMapId,
        space: spaces?.find(({ id }) => id === spaceId),
        reservation,
        selectedDate: formatDate(date),
      },
    });
  };

  const handleDeleteReservation = (reservationId: number, spaceId: number) => {
    if (selectedMapId === null) return;

    if (!window.confirm(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId: selectedMapId,
      spaceId,
      reservationId,
    });
  };

  const handleSubmitSlackUrl = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (selectedMapId === null) return;

    createSlackWebhookUrl.mutate({
      mapId: selectedMapId,
      slackUrl,
    });
  };

  const handleSubmitNotice = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (selectedMapId === null) return;

    setMapNotice.mutate({
      mapId: selectedMapId,
      notice: noticeText,
    });
  };

  useEffect(() => {
    const prevMapId = location.state?.mapId ?? null;
    const prevMapName = maps.find(({ mapId }) => mapId === prevMapId)?.mapName ?? '';

    if (isNullish(prevMapId)) {
      setSelectedMapId(maps.length ? maps[0].mapId : null);
      setSelectedMapName(maps.length ? maps[0].mapName : '');
      setNoticeText(maps.length ? maps[0].notice ?? '' : '');

      return;
    }

    setSelectedMapId(prevMapId);
    setSelectedMapName(prevMapName);
  }, [location.state?.mapId, maps, setNoticeText]);

  return (
    <>
      <Header />
      <Layout>
        <PageHeader
          title={selectedMapName}
          leftButtons={
            <IconButton text="맵 목록" size="small" onClick={handleOpenDrawer}>
              <MenuIcon width="100%" height="100%" />
            </IconButton>
          }
          rightButtons={
            selectedMapId !== null && (
              <>
                <Styled.RightIconButton
                  text="공지사항"
                  size="small"
                  onClick={() => setOpenedModal('notice')}
                >
                  <NoticeIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.RightIconButton
                  text="알림 설정"
                  size="small"
                  onClick={() => setOpenedModal('slack')}
                >
                  <SlackIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.VerticalBar />
                <Styled.RightIconButton
                  text="맵 편집"
                  size="small"
                  onClick={handleClickMapEditorIcon}
                >
                  <MapEditorIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.RightIconButton
                  text="공간 편집"
                  size="small"
                  onClick={handleClickSpaceEditorIcon}
                >
                  <SpaceEditorIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.VerticalBar />
                <Styled.RightIconButton text="공유 링크" size="small" onClick={handleCopyLink}>
                  <Styled.PrimaryLinkIcon width="100%" height="100%" />
                </Styled.RightIconButton>
              </>
            )
          }
        />
        <Styled.DateInputWrapper>
          <DateInput date={date} setDate={setDate} />
        </Styled.DateInputWrapper>

        <ReservationList
          reservations={reservations}
          selectedMapId={selectedMapId as number}
          isLoading={getReservations.isLoading}
          onCreateReservation={handleCreateReservation}
          onEditReservation={handleEditReservation}
          onDeleteReservation={handleDeleteReservation}
        />
      </Layout>

      {selectedMapId && maps && (
        <MapDrawer
          selectedMapId={selectedMapId}
          organization={organization}
          maps={maps}
          open={mapDrawerOpen}
          onCloseDrawer={handleCloseDrawer}
          onSelectMap={handleSelectMap}
          onDeleteMap={handleDeleteMap}
        />
      )}

      {openedModal === 'slack' && (
        <Modal open={openedModal === 'slack'} isClosableDimmer={true} onClose={closeModal}>
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
                <Button variant="text" type="button" onClick={closeModal}>
                  취소
                </Button>
                <Button variant="text">확인</Button>
              </Styled.ModalFooter>
            </form>
          </Modal.Inner>
        </Modal>
      )}

      {openedModal === 'notice' && (
        <Modal open={openedModal === 'notice'} isClosableDimmer={true} onClose={closeModal}>
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
                <Button variant="text" type="button" onClick={closeModal}>
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

export default ManagerMain;
