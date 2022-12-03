import styled from 'styled-components';

export const Page = styled.div`
  display: flex;
  flex-direction: column;
  height: calc(100vh - 3rem);
`;

export const PageWithBottomButton = styled.div<{ hasBottomButton: boolean }>`
  margin-bottom: ${({ hasBottomButton }) => (hasBottomButton ? '5rem' : '2rem')};
`;

export const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
  gap: 0.5rem;
  margin-top: 1rem;

  @media (max-width: 752px) {
    flex-direction: column;
  }
`;

export const PageHeaderElement = styled.div`
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
`;

export const MapInfo = styled(PageHeaderElement)`
  text-align: center;
  flex: 1;
`;

export const NoticeWrapper = styled(PageHeaderElement)`
  display: flex;
  align-items: center;
  flex: 2;
`;

export const Notice = styled.div``;

export const NoticeTitle = styled.span`
  color: ${({ theme }) => theme.primary[500]};
  font-weight: bold;
  display: inline-block;
  line-height: 1.25rem;
  margin-bottom: 0.5rem;
`;

export const NoticeText = styled.p`
  flex: 1;
  color: ${({ theme }) => theme.gray[500]};
  font-size: 0.875rem;
  line-height: 1.25rem;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0.5rem auto;
`;

export const MapContainer = styled.div`
  width: 100%;
  flex: 1;
  padding: 1rem;
  margin: 1rem 0 1.5rem;
  overflow: auto;
  display: flex;
  justify-content: center;
  position: relative;
`;

export const MapContainerInner = styled.div<{ width: number; height: number }>`
  min-width: ${({ width }) => width}px;
  min-height: ${({ height }) => height}px;
  display: flex;
  justify-content: center;

  @media (max-width: ${({ width }) => width}px) {
    position: absolute;
    top: 0;
    left: 0;
  }
`;

export const Space = styled.g`
  cursor: pointer;
`;

export const SpaceRect = styled.rect`
  &:hover {
    opacity: 0.5;
  }
`;

export const SpacePolygon = styled.polygon`
  &:hover {
    opacity: 0.5;
  }
`;

export const SpaceAreaText = styled.text`
  dominant-baseline: middle;
  text-anchor: middle;
  fill: ${({ theme }) => theme.black[700]};
  font-size: 1rem;
  pointer-events: none;
  user-select: none;
`;

export const SelectBox = styled.div`
  display: flex;
  flex-direction: column;
`;

export const SelectButton = styled.button`
  display: flex;
  align-items: center;
  border: none;
  cursor: pointer;
  background-color: white;
  padding: 1rem 1.5rem;
  text-align: left;
  font-size: 1rem;

  svg {
    margin-top: 0.25rem;
    margin-right: 0.5rem;
    fill: ${({ theme }) => theme.gray[400]};
  }

  &:hover {
    opacity: 0.7;
  }

  &:first-child {
    border-bottom: 1px solid rgba(196, 196, 196, 0.3);
  }
`;

export const DeleteModalContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: 1.5rem;
`;

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
