import styled from 'styled-components';

export const EmojiSelector = styled.div`
  position: relative;
  padding: 0.75rem;
  margin-top: 0.5rem;
  margin-bottom: 2rem;
  width: 100%;
  border-top: 1px solid ${({ theme }) => theme.gray[500]};
  background: none;
  outline: none;
  display: flex;
  justify-content: center;
`;

export const LabelText = styled.span`
  position: absolute;
  display: inline-block;
  top: -0.375rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0 0.25rem;
  font-size: 0.75rem;
  background-color: white;
  color: ${({ theme }) => theme.gray[500]};
`;

export const EmojiList = styled.div`
  margin-top: 1rem;
  font-size: 2.5rem;
  display: grid;
  grid-template-rows: repeat(2, 4rem);
  grid-template-columns: repeat(5, 4rem);
  gap: 1.25rem;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    font-size: 1.5rem;
    grid-template-rows: repeat(2, 3rem);
    grid-template-columns: repeat(5, 3rem);
    gap: 0.5rem;
  }
`;

export const EmojiItem = styled.label`
  position: relative;
  margin-bottom: 0;
  justify-self: center;
  align-self: center;
`;

export const EmojiCode = styled.div`
  cursor: pointer;
  border-radius: 999px;
  width: 4rem;
  height: 4rem;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  background-color: ${({ theme }) => theme.gray[100]};

  input:checked + & {
    background-color: ${({ theme }) => theme.primary[400]};
  }

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    width: 3rem;
    height: 3rem;
  }
`;

export const Radio = styled.input`
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  visibility: hidden;
`;
