import React, { useState } from 'react';
import { useHistory, useParams } from 'react-router-dom';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useGuestMap from 'hooks/query/useGuestMap';
import { MapDrawing, MapItem } from 'types/common';
import { GuestPageURLParams } from 'types/guest';
import GuestMap from './GuestMap';
import * as Styled from './GuestMap.styles';
import GuestMapFormProvider from './providers/GuestMapFormProvider';

const GuestMapContainer = () => {
  const { sharingMapId } = useParams<GuestPageURLParams>();
  const history = useHistory();

  const [map, setMap] = useState<MapItem | null>(null);

  useGuestMap(
    { sharingMapId },
    {
      onError: () => {
        history.replace(PATH.NOT_FOUND);
      },
      onSuccess: (response) => {
        const mapData = response.data;

        try {
          setMap({
            ...mapData,
            mapDrawing: JSON.parse(mapData.mapDrawing) as MapDrawing,
          });
        } catch (error) {
          console.error('Failed to parse mapDrawing:', error);
          history.replace(PATH.NOT_FOUND);
        }
      },
      retry: false,
      refetchOnWindowFocus: false,
      refetchOnMount: false,
      refetchOnReconnect: false,
      staleTime: Infinity,
    }
  );

  if (map === null) return <></>;

  return (
    <Styled.Page>
      <GuestMapFormProvider mapId={map.mapId}>
        <GuestMap map={map} />
      </GuestMapFormProvider>
    </Styled.Page>
  );
};

export default GuestMapContainer;
