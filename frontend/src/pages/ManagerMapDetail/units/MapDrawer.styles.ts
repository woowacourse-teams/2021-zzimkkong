import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { ReactComponent as Plus } from 'assets/svg/plus.svg';

export const SpaceWrapper = styled.div`
  margin: 2.5rem 0;
`;

export const PlusIcon = styled(Plus)`
  position: absolute;
  max-width: 4rem;
  max-height: 4rem;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;

export const CreateMapButton = styled(Link)`
  display: flex;
  justify-content: center;
  width: 100%;
  height: 0;
  padding-bottom: 75%;
  margin: 1rem auto;
  background-color: ${({ theme }) => theme.gray[50]};
  border: 1px solid ${({ theme }) => theme.gray[400]};
  border-radius: 0.25rem;
  position: relative;

  ${PlusIcon} {
    fill: ${({ theme }) => theme.gray[500]};
  }

  &:hover {
    ${PlusIcon} {
      fill: ${({ theme }) => theme.primary[400]};
    }
  }
`;
