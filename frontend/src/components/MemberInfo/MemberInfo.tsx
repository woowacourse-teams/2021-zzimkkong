import React from 'react';
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
        </Styled.NameTextContainer>
        {data?.data.organization && (
          <Styled.OranizationTextContainer>
            {data?.data.organization}
          </Styled.OranizationTextContainer>
        )}
      </Styled.InfoContainer>
    </Styled.Container>
  );
};

export default MemberInfo;
