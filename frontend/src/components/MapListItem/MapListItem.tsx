import { ReactNode } from 'react';
import * as Styled from './MapListItem.styles';

export interface Props {
  thumbnail: string;
  title: string;
  control?: ReactNode;
  selected?: boolean;
  onClick?: () => void;
}

const MapListItem = ({
  thumbnail,
  title,
  control,
  selected = false,
  onClick = () => null,
}: Props): JSX.Element => {
  return (
    <Styled.Container role="listitem" selected={selected}>
      <Styled.ImageWrapper onClick={onClick}>
        <Styled.ImageInner dangerouslySetInnerHTML={{ __html: `${thumbnail}` }} />
      </Styled.ImageWrapper>
      <Styled.TitleWrapper>
        <Styled.Title>{title}</Styled.Title>
        {control && <Styled.Control>{control}</Styled.Control>}
      </Styled.TitleWrapper>
    </Styled.Container>
  );
};

export default MapListItem;
