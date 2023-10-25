import { AxiosError } from 'axios';
import dayjs, { Dayjs } from 'dayjs';
import React, { useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { deleteManagerReservation } from 'api/managerReservation';
import { ReactComponent as BackIcon } from 'assets/svg/back.svg';
import { ReactComponent as MapEditorIcon } from 'assets/svg/map-editor.svg';
import { ReactComponent as SpaceEditorIcon } from 'assets/svg/space-editor.svg';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import SlackNotiButton from 'components/ManagerIconButtons/SlackNotiButton';
import PageHeader from 'components/PageHeader/PageHeader';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMapV2 from 'hooks/query-v2/useManagerMapV2';
import useManagerMapReservations from 'hooks/query/useManagerMapReservations';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import { Reservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ManagerMapDetail.styles';
import ReservationList from './units/ReservationList';

export interface ManagerMainState {
  targetDate?: Dayjs;
}

const ManagerMapDetail = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<ManagerMainState>();

  const params = useParams<{ mapId: string }>();
  const mapId = Number(params.mapId);
  const targetDate = location.state?.targetDate;

  const [date, setDate] = useState(targetDate ?? dayjs().tz());

  const getMap = useManagerMapV2(
    { mapId: mapId },
    {
      enabled: !isNullish(mapId),
      onError: (error: AxiosError<ErrorResponse>) => {
        alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
        history.replace(PATH.MANAGER_MAP_LIST);
      },
    }
  );

  const getSpaces = useManagerSpaces(
    {
      mapId: mapId,
    },
    {
      enabled: !isNullish(mapId),
    }
  );

  const getReservations = useManagerMapReservations(
    {
      mapId: mapId,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(mapId),
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

  const handleClickMapEditorIcon = () => {
    if (mapId == null) return;

    history.push(HREF.MANAGER_MAP_EDIT(mapId));
  };

  const handleClickSpaceEditorIcon = () => {
    if (mapId == null) return;

    history.push(HREF.MANAGER_SPACE_EDIT(mapId));
  };

  const handleCreateReservation = (spaceId: number) => {
    if (mapId === null) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION,
      state: {
        mapId: mapId,
        space: spaces?.find(({ id }) => id === spaceId),
        selectedDate: formatDate(date),
      },
    });
  };

  const handleEditReservation = (reservation: Reservation, spaceId: number) => {
    if (mapId === null) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION_EDIT,
      state: {
        mapId: mapId,
        space: spaces?.find(({ id }) => id === spaceId),
        reservation,
        selectedDate: formatDate(date),
      },
    });
  };

  const handleDeleteReservation = (reservationId: number, spaceId: number) => {
    if (mapId == null) return;

    if (!window.confirm(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId,
      spaceId,
      reservationId,
    });
  };

  return (
    <>
      <Header />
      <Layout>
        <PageHeader
          title={getMap.data?.data.mapName ?? ''}
          leftButtons={
            <IconButton
              size="small"
              onClick={() => {
                history.push(PATH.MANAGER_MAP_LIST);
              }}
            >
              <BackIcon width="100%" height="100%" />
            </IconButton>
          }
          rightButtons={
            getMap.data?.data != null && (
              <>
                <SlackNotiButton text="알림 설정" size="small" map={getMap.data.data} />
                <Styled.VerticalBar />
                <IconButton text="맵 편집" size="small" onClick={handleClickMapEditorIcon}>
                  <MapEditorIcon width="100%" height="100%" />
                </IconButton>
                <IconButton text="공간 편집" size="small" onClick={handleClickSpaceEditorIcon}>
                  <SpaceEditorIcon width="100%" height="100%" />
                </IconButton>
              </>
            )
          }
        />
        <Styled.DateInputWrapper>
          <DateInput date={date} setDate={setDate} />
        </Styled.DateInputWrapper>

        <ReservationList
          reservations={reservations}
          selectedMapId={mapId}
          isLoading={getReservations.isLoading}
          onCreateReservation={handleCreateReservation}
          onEditReservation={handleEditReservation}
          onDeleteReservation={handleDeleteReservation}
        />
      </Layout>
    </>
  );
};

export default ManagerMapDetail;
