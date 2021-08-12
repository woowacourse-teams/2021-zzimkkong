import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import { ReactComponent as Plus } from 'assets/svg/plus.svg';
import IconButton from 'components/IconButton/IconButton';

export const PrimaryLinkIcon = styled(LinkIcon)`
  fill: ${({ theme }) => theme.primary[400]};
`;

export const RightIconButton = styled(IconButton)`
  margin: 0 0.25rem;

  &:first-child {
    margin-left: 0;
  }

  &:last-child {
    margin-right: 0;
  }
`;

export const VerticalBar = styled.div`
  width: 1px;
  height: 1.5rem;
  margin: 0 0.25rem;
  background-color: ${({ theme }) => theme.gray[400]};
`;

export const DateInputWrapper = styled.div`
  margin: 1rem 0;
`;

export const NoticeWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 22rem);
`;

export const NoticeMessage = styled.p`
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
`;

export const NoticeLink = styled(Link)`
  font-size: 1.25rem;
  text-decoration: none;
  color: ${({ theme }) => theme.primary[400]};
`;

export const PanelMessage = styled.p`
  padding: 1rem 0.75rem;
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const SpaceList = styled.ul`
  margin: 3rem 0;
`;

export const SpaceReservationWrapper = styled.div``;

export const SpaceWrapper = styled.div`
  margin: 2.5rem 0;
`;

export const PlusIcon = styled(Plus)`
  position: absolute;
  max-width: 4rem;
  max-height: 4rem;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;

export const CreateMapButton = styled(Link)`
  display: flex;
  justify-content: center;
  width: 100%;
  height: 0;
  padding-bottom: 75%;
  margin: 1rem auto;
  background-color: ${({ theme }) => theme.gray[50]};
  border: 1px solid ${({ theme }) => theme.gray[400]};
  border-radius: 0.25rem;
  position: relative;

  ${PlusIcon} {
    fill: ${({ theme }) => theme.gray[500]};
  }

  &:hover {
    ${PlusIcon} {
      fill: ${({ theme }) => theme.primary[400]};
    }
  }
`;
