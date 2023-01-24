import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  align-items: center;
  padding: 0.875rem;
  border-bottom: 1px solid ${({ theme }) => theme.gray[300]};

  :last-of-type {
    border-bottom: 0;
  }
`;

export const MapInfo = styled.div`
  display: flex;
  align-items: center;
  cursor: pointer;
  flex: 1;
`;

export const ImageWrapper = styled.div`
  display: flex;
  align-items: center;
  width: 120px;
  height: 120px;
  margin-right: 36px;
`;

export const ImageInner = styled.div`
  width: 100%;

  svg {
    width: 100% !important;
    height: 100% !important;
  }
`;

export const TitleWrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const Title = styled.h3`
  font-size: 1.125rem;
  margin-right: 0.5rem;
`;

export const ControlWrapper = styled.div`
  display: flex;
  align-items: center;

  & > * {
    margin-right: 1.25rem;

    :last-of-type {
      margin-right: 0;
    }
  }
`;
