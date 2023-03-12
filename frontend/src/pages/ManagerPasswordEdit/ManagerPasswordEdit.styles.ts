import styled from 'styled-components';
import { FORM_MAX_WIDTH } from 'constants/style';

export const ContainerForm = styled.form`
  width: 100%;
  max-width: ${FORM_MAX_WIDTH};
  margin: 0 auto;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 400;
  text-align: center;
  margin: 2.125rem auto;
`;

export const PasswordChangeLinkMessage = styled.p`
  margin: 1rem 0;
  text-align: center;
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[500]};

  a {
    color: ${({ theme }) => theme.primary[400]};
    text-decoration: none;
    margin-left: 0.375rem;

    &:hover {
      font-weight: 700;
    }
  }
`;

export const InputWrapper = styled.div`
  margin-bottom: 2rem;
`;

export const ButtonContainer = styled.div`
  display: flex;

  *:first-child {
    margin-right: 0.5rem;
  }
`;
