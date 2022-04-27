import styled from 'styled-components';

interface ContainerProps {
  selected: boolean;
}

export const Container = styled.div<ContainerProps>`
  padding: 0.75rem;
  border: 2px solid ${({ theme, selected }) => (selected ? theme.primary[400] : theme.gray[400])};
  border-radius: 0.25rem;
`;

export const ImageWrapper = styled.div`
  width: 100%;
  height: 0;
  padding-bottom: 75%;
  position: relative;

  &:hover {
    cursor: pointer;
  }
`;

export const ImageInner = styled.div`
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  svg {
    z-index: -1;
  }
`;

export const TitleWrapper = styled.div`
  display: flex;
  gap: 0.25rem;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0 0;
`;

export const Title = styled.h3`
  font-size: 1rem;
  line-height: 1.375rem;
`;

export const Control = styled.div`
  display: flex;
  gap: 0.125rem;
  align-items: center;
  justify-content: flex-end;
`;
