import styled from 'styled-components';

interface PanelProps {
  expanded: boolean;
}

export const Panel = styled.div<PanelProps>`
  margin: ${({ expanded }) => (expanded ? '1.5rem 0' : '0')};

  &:first-of-type {
    margin-top: 0;
  }

  &:last-of-type {
    margin-bottom: 0;
  }
`;

export const Title = styled.p`
  display: inline-block;
`;
