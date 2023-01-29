import styled from 'styled-components';
import Button from 'components/Button/Button';

export const Container = styled.div`
  padding: 60px 0 80px;
`;

export const List = styled.div`
  margin: 28px 0 0;
`;

export const ButtonContainer = styled.div`
  margin: 36px 0;
  display: flex;
  justify-content: center;
`;

export const RoundedButton = styled(Button)`
  border: 1px solid ${({ theme }) => theme.gray[400]};
  padding: 12px 48px;
  font-size: 1.125rem;

  :hover {
    background-color: ${({ theme }) => theme.gray[100]};
  }
`;

export const FlexLeft = styled.div`
  display: flex;
  justify-content: flex-end;
`;

export const HorizontalLine = styled.hr`
  margin: 1.5rem 0;
`;

export const Form = styled.form`
  margin: 3.75rem 0 1rem;

  label {
    margin-bottom: 3rem;
  }
`;
