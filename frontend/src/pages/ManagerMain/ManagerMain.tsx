import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteMap } from 'api/managerMap';
import { deleteManagerReservation } from 'api/managerReservation';
import { ReactComponent as MapEditorIcon } from 'assets/svg/map-editor.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as SpaceEditorIcon } from 'assets/svg/space-editor.svg';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMaps from 'hooks/query/useManagerMaps';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import { Reservation } from 'types/common';
import { ErrorResponse, MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ManagerMain.styles';
import MapDrawer from './units/MapDrawer';
import ReservationList from './units/ReservationList';

export interface ManagerMainState {
  mapId?: number;
  targetDate?: Date;
}

const ManagerMain = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<ManagerMainState>();

  const mapId = location.state?.mapId;
  const targetDate = location.state?.targetDate;

  const [date, setDate] = useState(targetDate ?? new Date());
  const [open, setOpen] = useState(false);

  const [selectedMapId, setSelectedMapId] = useState<number | null>(mapId ?? null);
  const [selectedMapName, setSelectedMapName] = useState('');

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

  const removeReservation = useMutation(deleteManagerReservation, {
    onSuccess: () => {
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

  const handleDeleteMap = (mapId: number) => {
    removeMap.mutate({ mapId });
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
    setOpen(true);
  };

  const handleCloseDrawer = () => {
    setOpen(false);
  };

  const handleSelectMap = (mapId: number, mapName: string) => {
    setSelectedMapId(mapId);
    setSelectedMapName(mapName);
    handleCloseDrawer();
  };

  const handleCreateReservation = (spaceId: number) => {
    if (!selectedMapId) return;

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
    if (!selectedMapId) return;

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
    if (!selectedMapId) return;

    if (!window.confirm(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId: selectedMapId,
      spaceId,
      reservationId,
    });
  };

  useEffect(() => {
    const prevMapId = location.state?.mapId ?? null;
    const prevMapName = maps.find(({ mapId }) => mapId === prevMapId)?.mapName ?? '';

    if (isNullish(prevMapId)) {
      setSelectedMapId(maps.length ? maps[0].mapId : null);
      setSelectedMapName(maps.length ? maps[0].mapName : '');

      return;
    }

    setSelectedMapId(prevMapId);
    setSelectedMapName(prevMapName);
  }, [location.state?.mapId, maps]);

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
          selectedMapId={selectedMapId as number}
          date={date}
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
          open={open}
          onCloseDrawer={handleCloseDrawer}
          onSelectMap={handleSelectMap}
          onDeleteMap={handleDeleteMap}
        />
      )}
    </>
  );
};

export default ManagerMain;
