import styled from 'styled-components';

export const PageHeader = styled.h2`
  font-size: 1.625rem;
  font-weight: 700;
  margin: 0.75rem 0;
`;

export const DateWrapper = styled.label`
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 13rem;
  gap: 0.5rem;
  padding: 1rem 0;
  margin: 1.5rem 0;
  z-index: 1;

  svg {
    position: absolute;
    right: 0;
    pointer-events: none;
  }
`;

export const DateInput = styled.input`
  position: absolute;
  display: block;
  width: 100%;
  font-weight: 700;
  font-size: 1.5rem;
  color: ${({ theme }) => theme.gray[500]};
  border: none;
  line-height: 1em;

  &::-webkit-calendar-picker-indicator {
    position: absolute;
    top: -150%;
    left: -150%;
    width: 300%;
    height: 300%;
    cursor: pointer;
  }
`;

export const PanelMessage = styled.p`
  padding: 1rem 0.75rem;
`;

export const SpaceList = styled.ul`
  margin: 1rem 0;
`;

export const SpaceListItem = styled.li`
  margin: 0.75rem 0;
`;
