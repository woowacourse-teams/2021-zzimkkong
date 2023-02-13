import styled from 'styled-components';

interface TitleProps {
  hasLeftButton: boolean;
}

export const Container = styled.div`
  display: flex;
  align-items: center;
  margin: 1rem 0;
`;

export const ButtonContainer = styled.div`
  display: flex;
  align-items: center;

  & > * {
    margin-right: 0.75rem;

    :last-child {
      margin-right: 0;
    }
  }
`;

export const Title = styled.h2<TitleProps>`
  font-size: 1.25rem;
  padding: ${({ hasLeftButton }) => (hasLeftButton ? '0 0.75rem' : '0 0.75rem 0 0')};
  flex-grow: 1;
`;
