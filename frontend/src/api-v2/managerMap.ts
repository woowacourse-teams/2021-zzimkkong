import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import { QueryManagerMapsSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export const queryManagerMapsV2: QueryFunction<AxiosResponse<QueryManagerMapsSuccessV2>> = () =>
  apiV2.get('/api/maps');
