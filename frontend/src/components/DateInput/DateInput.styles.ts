import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 0.25rem;
  background-color: ${({ theme }) => theme.gray[100]};
  padding: 0.75rem 0.25rem;
`;

export const DateWrapper = styled.label`
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 10.5rem;
  gap: 0.5rem;

  svg {
    position: absolute;
    right: 0;
    pointer-events: none;
  }
`;

export const DateText = styled.div``;

export const DateInput = styled.input`
  position: absolute;
  display: block;
  width: 100%;
  color: transparent;
  border: none;
  background: none;
  line-height: 1em;
  padding: 0.25rem 0;

  &::-webkit-calendar-picker-indicator {
    position: absolute;
    top: -150%;
    left: -150%;
    width: 300%;
    height: 300%;
    cursor: pointer;
  }
`;
