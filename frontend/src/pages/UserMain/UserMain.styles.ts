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

  @media (max-width: ${(props) => props.theme.breakpoints.md}px) {
    height: auto;
  }
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
  display: block;
  background: ${({ theme }) => theme.primary[400]};
  color: ${({ theme }) => theme.white};
  border-right: 1px solid ${({ theme }) => theme.black[400]};
  margin-right: 1rem;
  text-decoration: none;
  float: left;
  line-height: 1.1em;
`;

export const ReservationList = styled.div`
  & > [role='listitem'] {
    border-bottom: 1px solid ${({ theme }) => theme.black[400]};

    &:last-of-type {
      border: 0;
    }
  }
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
  font-size: 1.25rem;

  svg {
    margin-top: 0.25rem;
    margin-right: 0.5rem;
  }

  &:hover {
    opacity: 0.7;
  }

  &:first-child {
    border-bottom: 1px solid rgba(196, 196, 196, 0.3);
  }
`;

export const Message = styled.p``;
