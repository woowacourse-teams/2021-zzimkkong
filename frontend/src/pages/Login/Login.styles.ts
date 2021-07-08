import styled from 'styled-components';

export const Container = styled.div`
  width: 100%;
  max-width: 660px;
  margin: 0 auto;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 700;
  text-align: center;
  margin: 1.5rem auto;
`;

export const Form = styled.form`
  label {
    margin: 2.5rem 0;
  }
`;

export const JoinMessage = styled.p`
  margin: 1rem 0;
  text-align: center;

  a {
    color: ${({ theme }) => theme.primary[400]};
    text-decoration: none;
    margin-left: 0.375rem;

    &:hover {
      font-weight: 700;
    }
  }
`;
