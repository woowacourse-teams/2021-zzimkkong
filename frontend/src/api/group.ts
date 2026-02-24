import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { GroupOption } from 'types/common';
import api from './api';

export const queryGroups: QueryFunction<AxiosResponse<GroupOption[]>, QueryKey> = () => {
  return api.get('/groups');
};
