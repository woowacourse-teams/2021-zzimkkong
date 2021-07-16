import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryReservationsSuccess } from 'types/response';
import api from './api';

export interface QueryReservationsParams {
  mapId: number;
  spaceId: number;
  date: string;
}

interface PostReservationParams {
  reservation: {
    spaceId: number;
    startDateTime: Date;
    endDateTime: Date;
    password: string;
    name: string;
    description: string;
  };
  mapId: number;
}

export const queryReservations: QueryFunction<
  AxiosResponse<QueryReservationsSuccess>,
  [QueryKey, QueryReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId, date } = data;

  return api.get(`/maps/${mapId}/spaces/${spaceId}/reservations?date=${date}`);
};

export const postReservation = ({
  reservation,
  mapId,
}: PostReservationParams): Promise<AxiosResponse<never>> =>
  api.post(`/maps/${mapId}/reservations`, reservation);
