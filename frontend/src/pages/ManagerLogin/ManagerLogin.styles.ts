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

export const JoinLinkMessage = styled.p`
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

export const HorizontalLine = styled.hr`
  margin: 1.5rem 0;
`;

export const SocialLogin = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin: 1.5rem 0;
`;
