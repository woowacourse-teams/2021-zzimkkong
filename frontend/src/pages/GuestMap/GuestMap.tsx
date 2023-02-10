import { AxiosError } from 'axios';
import dayjs, { Dayjs } from 'dayjs';
import { useContext, useEffect, useMemo, useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { deleteGuestReservation } from 'api/guestReservation';
import Header from 'components/Header/Header';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { Area, MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { GuestPageURLParams } from 'types/guest';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './GuestMap.styles';
import { GuestMapFormContext } from './providers/GuestMapFormProvider';
import Aside from './units/Aside';
import GuestMapDrawing from './units/GuestMapDrawing';
import LoginPopup from './units/LoginPopup';
import PasswordInputModal from './units/PasswordInputModal';

export const SWITCH_LABEL_LIST = ['예약하기', '예약현황'];

export interface GuestMapState {
  spaceId?: Space['id'];
  targetDate?: Dayjs;
  scrollPosition?: ScrollPosition;
}

interface GuestMapProps {
  map: MapItem;
}

const GuestMap = ({ map }: GuestMapProps): JSX.Element => {
  const { accessToken } = useContext(AccessTokenContext);
  const { setSelectedSpaceId: setSelectedSpaceIdForm } = useContext(GuestMapFormContext);

  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation>();

  const [loginPopupOpen, setLoginPopupOpen] = useState(true);

  const history = useHistory();
  const location = useLocation<GuestMapState>();
  const { sharingMapId } = useParams<GuestPageURLParams>();

  const mapRef = useRef<HTMLDivElement | null>(null);

  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;
  const scrollPosition = location.state?.scrollPosition;

  const mapDrawing = map.mapDrawing;

  const [spaceList, setSpaceList] = useState<Space[]>([]);
  const [selectedSpaceId, setSelectedSpaceId] = useState<Space['id'] | null>(spaceId ?? null);
  const [date, setDate] = useState(targetDate ? dayjs(targetDate).tz() : dayjs().tz());

  const [selectedSwitchLabel, setSelectedSwitchLabel] = useState<typeof SWITCH_LABEL_LIST[number]>(
    SWITCH_LABEL_LIST[0]
  );

  const spaces = useMemo(() => {
    const result: { [key: string]: Space } = {};
    spaceList.forEach((item) => (result[item.id] = item));

    return result;
  }, [spaceList]);

  useGuestSpaces(
    { mapId: map.mapId },
    {
      enabled: map.mapId !== undefined,
      onSuccess: (response) => {
        const { spaces } = response.data;

        setSpaceList(spaces.map((space) => ({ ...space, area: JSON.parse(space.area) as Area })));

        if (selectedSpaceId === null) {
          setSelectedSpaceId(spaces[0].id);
        }
      },
    }
  );

  const getReservations = useGuestReservations(
    {
      mapId: map.mapId,
      spaceId: selectedSpaceId as number,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(selectedSpaceId) && dayjs(date).isValid(),
    }
  );

  const removeReservation = useMutation(deleteGuestReservation, {
    onSuccess: () => {
      getReservations.refetch();
      window.alert(MESSAGE.RESERVATION.DELETE_SUCCESS);
      setPasswordInputModalOpen(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const handleClickSwitch = (label: typeof SWITCH_LABEL_LIST[number]) => {
    setSelectedSwitchLabel(label);
  };

  const handleClickSpaceArea = (spaceId: number) => {
    if (selectedSwitchLabel === SWITCH_LABEL_LIST[0]) {
      setSelectedSpaceIdForm?.(`${spaceId}`);
    } else if (selectedSwitchLabel === SWITCH_LABEL_LIST[1]) {
      setSelectedSpaceId(spaceId);
    }
  };

  const deleteLoginReservation = (reservationId: number) => {
    if (typeof map.mapId !== 'number' || selectedSpaceId === null) return;

    if (!window.confirm(MESSAGE.GUEST_MAP.DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId: map.mapId,
      spaceId: selectedSpaceId,
      reservationId: reservationId,
    });
  };

  const handleDeleteGuestReservation = (passwordInput: string) => {
    if (typeof map.mapId !== 'number' || selectedSpaceId === null) return;

    removeReservation.mutate({
      mapId: map.mapId,
      spaceId: selectedSpaceId,
      password: passwordInput,
      reservationId: Number(selectedReservation?.id),
    });
  };

  const handleDelete = (reservation: Reservation) => {
    if (!reservation.isLoginReservation) {
      setPasswordInputModalOpen(true);
      setSelectedReservation(reservation);
    } else {
      deleteLoginReservation(reservation.id);
    }
  };

  const handleEdit = (reservation: Reservation) => {
    if (!selectedSpaceId) return;

    history.push({
      pathname: HREF.GUEST_RESERVATION_EDIT(sharingMapId),
      state: {
        mapId: map.mapId,
        spaceId: spaces[selectedSpaceId].id,
        reservation,
        selectedDate: formatDate(date),
        scrollPosition: { x: mapRef?.current?.scrollLeft, y: mapRef?.current?.scrollTop },
      },
    });
  };

  const handleLogin = () => {
    getReservations.refetch();
  };

  useEffect(() => {
    if (scrollPosition) {
      mapRef.current?.scrollTo(scrollPosition.x ?? 0, scrollPosition.y ?? 0);
    }
  }, [scrollPosition]);

  useEffect(() => {
    if (targetDate) {
      setDate(dayjs(targetDate).tz());
    }
  }, [targetDate]);

  return (
    <>
      {/* TODO HeaderWrapper가 Aside의 length에 의존하고 있음 공통값으로 관리해야 함 */}
      <Aside
        map={map}
        selectedLabel={selectedSwitchLabel}
        onClickSwitch={handleClickSwitch}
        selectedSpaceId={selectedSpaceId}
      />
      <Styled.MapContainer ref={mapRef}>
        <Styled.HeaderWrapper>
          <Header onClickLogin={() => setLoginPopupOpen(true)} />
        </Styled.HeaderWrapper>
        {mapDrawing && (
          <GuestMapDrawing
            isReservation={selectedSwitchLabel === SWITCH_LABEL_LIST[0]}
            mapDrawing={mapDrawing}
            spaceList={spaceList}
            onClickSpaceArea={handleClickSpaceArea}
          />
        )}
      </Styled.MapContainer>

      <PasswordInputModal
        open={passwordInputModalOpen}
        onClose={() => setPasswordInputModalOpen(false)}
        onSubmit={handleDeleteGuestReservation}
      />

      {!accessToken && (
        <LoginPopup
          open={loginPopupOpen}
          onClose={() => setLoginPopupOpen(false)}
          onLogin={handleLogin}
        />
      )}
    </>
  );
};

export default GuestMap;
