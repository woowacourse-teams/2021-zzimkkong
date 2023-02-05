import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid ${({ theme }) => theme.gray[300]};

  &:last-of-type {
    border-bottom: 0;
  }
`;

export const InfoContainer = styled.div`
  display: flex;
  flex: 1;
  cursor: pointer;
`;

export const Description = styled.div`
  font-weight: bold;
  min-width: 300px;
  margin-right: 36px;
`;

export const DetailContainer = styled.div`
  display: flex;
  flex-direction: column;
  flex: 1;
`;

export const Date = styled.div`
  font-size: 1.125rem;
  margin-bottom: 1rem;
`;

export const Space = styled.div<{ spaceColor: string }>`
  display: flex;
  align-items: center;
  font-size: 1.125rem;
  margin-right: 0.5rem;

  ::before {
    content: '';
    display: block;
    width: 20px;
    height: 20px;
    margin-top: 4px;
    margin-right: 1rem;
    border-radius: 50%;
    background-color: ${({ spaceColor }) => spaceColor};
  }
`;

export const ControlWrapper = styled.div`
  display: flex;
  align-items: center;

  & > * {
    margin-right: 1.25rem;

    :last-of-type {
      margin-right: 0;
    }
  }
`;
