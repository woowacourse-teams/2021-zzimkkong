import { Link } from 'react-router-dom';
import styled from 'styled-components';

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 700;
  margin: 1.5rem auto;
`;

export const MapContainer = styled.div`
  width: 100%;
  height: 50vh;
  padding: 1rem;
  margin: 1rem 0 1.5rem;
  overflow: auto;
  display: flex;
  justify-content: center;
`;

export const Map = styled.div`
  width: inherit;
  height: inherit;
  position: relative;
`;

export const PanelContainer = styled.div`
  margin: 1.5rem 0;
`;

export const ReservationLink = styled(Link)`
  display: inline-block;
  padding: 0.625rem 0.875rem;
  background: ${({ theme }) => theme.primary[400]};
  color: ${({ theme }) => theme.white};
  border-right: 1px solid ${({ theme }) => theme.black[400]};
  margin-right: 1rem;
  text-decoration: none;
`;

export const ReservationList = styled.div`
  & > [role='listitem'] {
    border-bottom: 1px solid ${({ theme }) => theme.black[400]};

    &:last-of-type {
      border: 0;
    }
  }
`;
