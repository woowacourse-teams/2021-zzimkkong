import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import IconButton from 'components/IconButton/IconButton';

export const PageHeader = styled.div`
  padding: 0.75rem 0;
  display: flex;
  align-items: center;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 500;
  padding: 0 0.5rem;
  flex-grow: 1;
  text-align: center;
`;

export const PrimaryLinkIcon = styled(LinkIcon)`
  fill: ${({ theme }) => theme.primary[400]};
`;

export const DateInputWrapper = styled.div`
  margin: 1rem 0;
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

export const MapListItemControlButton = styled(IconButton)`
  & > svg {
    fill: ${({ theme }) => theme.gray[500]};

    &:hover {
      fill: ${({ theme }) => theme.primary[400]};
    }
  }
`;

export const CreateMapButton = styled(Link)`
  display: flex;
  justify-content: center;
  width: 5rem;
  height: 5rem;
  padding: 1rem;
  margin: 1rem auto;
  background-color: ${({ theme }) => theme.gray[100]};
  border-radius: 50%;

  & > svg {
    fill: ${({ theme }) => theme.gray[500]};
  }

  &:hover {
    & > svg {
      fill: ${({ theme }) => theme.primary[400]};
    }
  }
`;
