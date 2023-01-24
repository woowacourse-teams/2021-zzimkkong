import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  align-items: center;
`;

export const EmojiContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: flex-end;
  width: 112px;
  height: 112px;
  padding-bottom: 10px;
  margin-right: 40px;
  border-radius: 50%;
  background-color: ${({ theme }) => theme.primary[100]};
  overflow: hidden;
`;

export const Emoji = styled.div`
  font-size: 96px;
`;

export const InfoContainer = styled.div`
  display: flex;
  flex-direction: column;

  & > * {
    margin-bottom: 12px;

    ::last-of-type {
      margin-bottom: 0;
    }
  }
`;

export const NameTextContainer = styled.div`
  font-size: 1.5rem;
`;

export const NameText = styled.span`
  font-weight: bold;
  margin-right: 0.375rem;
`;

export const OranizationTextContainer = styled.div`
  font-size: 0.875rem;
`;
