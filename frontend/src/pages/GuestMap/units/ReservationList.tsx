import React from 'react';
import { MapItem } from 'types/common';

interface Props {
  map: MapItem;
}

const ReservationList = ({ map }: Props) => {
  return <div>{map.mapId}</div>;
};

export default ReservationList;
