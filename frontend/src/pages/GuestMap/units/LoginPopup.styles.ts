import styled from 'styled-components';

export const LoginPopupWrapper = styled.div`
  padding: 2rem 3rem;
`;

export const LoginPopupHeading = styled.h2`
  font-size: 1.5rem;
  text-align: center;
  margin-bottom: 1.5rem;

  strong {
    font-weight: bold;
  }

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.md}px) {
    font-size: 1.125rem;
  }
`;

export const LoginPopupForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

export const LoginFormInputWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

export const LoginFormButtonWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

export const Line = styled.hr`
  height: 1px;
  border-width: 0px;
  color: ${({ theme }) => theme.gray[300]};
  background-color: ${({ theme }) => theme.gray[300]};
  margin: 1rem 0;
`;

export const SocialLoginButtonWrapper = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
`;

export const ContinueWithNonMemberWrapper = styled.div`
  text-align: center;
`;

export const ContinueWithNonMember = styled.a`
  display: inline-block;
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[400]};
  text-decoration: underline;
  cursor: pointer;
`;
