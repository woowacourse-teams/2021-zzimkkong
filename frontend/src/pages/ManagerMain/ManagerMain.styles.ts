import styled from 'styled-components';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import IconButton from 'components/IconButton/IconButton';

export const PrimaryLinkIcon = styled(LinkIcon)`
  fill: ${({ theme }) => theme.primary[400]};
`;

export const RightIconButton = styled(IconButton)`
  margin: 0 0.25rem;

  &:first-child {
    margin-left: 0;
  }

  &:last-child {
    margin-right: 0;
  }
`;

export const VerticalBar = styled.div`
  width: 1px;
  height: 1.5rem;
  margin: 0 0.25rem;
  background-color: ${({ theme }) => theme.gray[400]};
`;

export const DateInputWrapper = styled.div`
  margin: 1rem 0;
`;

export const ButtonText = styled.span`
  margin-left: 0.25rem;
`;

export const SlackModalContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: 1.5rem;
`;
