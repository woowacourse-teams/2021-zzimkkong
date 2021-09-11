import styled, { css } from 'styled-components';

interface WeekdayProps {
  value?: 'Saturday' | 'Sunday';
}

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const Label = styled.label`
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
`;

const weekdayColorCSS = {
  default: css``,
  Saturday: css`
    color: ${({ theme }) => theme.blue[900]};
  `,
  Sunday: css`
    color: ${({ theme }) => theme.red[500]};
  `,
};

export const DisplayName = styled.span<WeekdayProps>`
  ${({ value }) => weekdayColorCSS[value ?? 'default']};
`;
