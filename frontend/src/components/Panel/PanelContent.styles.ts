import styled from 'styled-components';

interface ContentProps {
  expanded: boolean;
}

export const Content = styled.div<ContentProps>`
  padding: 0 0.75rem;
  border-bottom: ${({ expanded, theme }) =>
    expanded ? `1px solid ${theme.black[400] as string}` : 'none'};
`;
