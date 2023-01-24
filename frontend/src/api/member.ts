import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import { Emoji } from 'types/common';
import api from './api';

export interface QueryMemberSuccess {
  id: number;
  email: string;
  userName: string;
  emoji: Emoji;
  organization: string | null;
}

export const queryMember: QueryFunction<AxiosResponse<QueryMemberSuccess>> = () => {
  return api.get('/members/me');
};
