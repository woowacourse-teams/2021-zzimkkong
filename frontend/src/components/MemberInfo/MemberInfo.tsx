import React from 'react';
import { Link } from 'react-router-dom';
import { theme } from 'App.styles';
import { ReactComponent as CaretIcon } from 'assets/svg/caret-right.svg';
import PATH from 'constants/path';
import useMember from 'hooks/query/useMember';
import * as Styled from './MemberInfo.styled';

const MemberInfo = (): JSX.Element => {
  const { data } = useMember();

  return (
    <Styled.Container>
      <Styled.EmojiContainer>
        <Styled.Emoji>{data?.data.emoji.code}</Styled.Emoji>
      </Styled.EmojiContainer>
      <Styled.InfoContainer>
        <Styled.NameTextContainer>
          <Styled.NameText>{data?.data.userName}</Styled.NameText>님
          <Link to={PATH.MANAGER_PROFILE_EDIT}>
            <CaretIcon width={36} height={36} fill={theme.gray[400]} />
          </Link>
        </Styled.NameTextContainer>
        {data?.data.organization && (
          <Styled.OrganizationTextContainer>
            {data?.data.organization}
          </Styled.OrganizationTextContainer>
        )}
      </Styled.InfoContainer>
    </Styled.Container>
  );
};

export default MemberInfo;
