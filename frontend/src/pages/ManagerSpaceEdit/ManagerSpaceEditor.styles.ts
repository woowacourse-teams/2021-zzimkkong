import styled from 'styled-components';
import { Z_INDEX } from 'constants/style';

interface FormContainerProps {
  disabled: boolean;
}

export const Page = styled.div`
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  height: calc(100vh - 3rem);
`;

export const EditorMain = styled.div`
  flex: 1;
  display: flex;
  justify-content: space-between;
  gap: 1.5rem;
  overflow: hidden;
`;

export const EditorContainer = styled.div`
  position: relative;
  display: flex;
  flex: 2;
  height: 100%;
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.25rem;
`;

export const FormContainer = styled.div<FormContainerProps>`
  position: relative;
  display: flex;
  flex-direction: column;
  flex: 1;
  height: 100%;
  min-width: 22rem;
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.25rem;

  &::before {
    ${({ disabled }) => (disabled ? `content: ''` : '')};
    position: absolute;
    top: 0;
    left: 0;
    display: block;
    width: 100%;
    height: 100%;
    background-color: ${({ theme }) => theme.gray[200]};
    opacity: 0.3;
    z-index: ${Z_INDEX.MODAL_OVERLAY};
  }
`;

export const AddButtonWrapper = styled.div`
  display: inline-block;
  position: absolute;
  right: 1.5rem;
  bottom: -1.25rem;
  z-index: ${Z_INDEX.SPACE_ADD_BUTTON};
`;

export const NoSpaceMessage = styled.p`
  text-align: center;
  font-size: 1.125rem;
  margin: 3rem auto;
`;
