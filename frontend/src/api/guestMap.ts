import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryGuestMapSuccess } from 'types/response';
import api from './api';

export interface QueryGuestMapParams {
  sharingMapId: string;
}

export const queryGuestMap: QueryFunction<
  AxiosResponse<QueryGuestMapSuccess>,
  [QueryKey, QueryGuestMapParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { sharingMapId } = data;

  return api.get(`/guests/maps?sharingMapId=${sharingMapId}`);
};
