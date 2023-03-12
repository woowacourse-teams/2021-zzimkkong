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

export interface PutMemberParams {
  userName: string;
  emoji: string;
}

export interface PutMemberPasswordParams {
  oldPassword: string;
  newPassword: string;
  newPasswordConfirm: string;
}

export const queryMember: QueryFunction<AxiosResponse<QueryMemberSuccess>> = () => {
  return api.get('/members/me');
};

export const putMember = ({ userName, emoji }: PutMemberParams) => {
  return api.put('/members/me', {
    userName,
    emoji,
  });
};

export const putMemberPassword = (params: PutMemberPasswordParams) => {
  return api.put('members/me/password', params);
};
