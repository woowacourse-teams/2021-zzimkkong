import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import Button from 'components/Button/Button';
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

export const ReservationsContainer = styled.div`
  margin: 2rem 0 5rem;
`;

export const SpacesOrderButton = styled(Button)`
  display: block;
  margin-left: auto;
  color: ${({ theme }) => theme.gray[400]};

  &:hover {
    color: ${({ theme }) => theme.gray[500]};
  }
`;

export const SpaceList = styled.ul``;

export const IconButtonWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
`;

export const PanelHeadWrapper = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
`;
