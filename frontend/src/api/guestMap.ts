import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryGuestMapSuccess } from 'types/response';
import api from './api';

export interface QueryGuestMapParams {
  publicMapId: string;
}

export const queryGuestMap: QueryFunction<
  AxiosResponse<QueryGuestMapSuccess>,
  [QueryKey, QueryGuestMapParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { publicMapId } = data;

  return api.get(`/guests/maps?publicMapId=${publicMapId}`);
};
