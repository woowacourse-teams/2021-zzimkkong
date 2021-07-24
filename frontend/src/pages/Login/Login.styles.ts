import styled from 'styled-components';

export const Container = styled.div`
  width: 100%;
  max-width: 660px;
  margin: 0 auto;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 400;
  text-align: center;
  margin: 2.125rem auto;
`;

export const Form = styled.form`
  margin: 3.75rem 0;

  label {
    margin-bottom: 3rem;
  }

  label:nth-last-of-type(1) {
    margin-bottom: 0;
  }
`;

export const LoginErrorMessage = styled.p`
  padding: 1.5rem 0;
  color: ${({ theme }) => theme.red[500]};
`;

export const JoinLinkMessage = styled.p`
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
