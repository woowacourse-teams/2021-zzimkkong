import { ReactElement } from 'react';
import * as Styled from './SpaceListItem.styles';

export interface Props {
  thumbnail: {
    src: string;
    alt: string;
  };
  title: string;
  control?: ReactElement;
}

const SpaceListItem = ({ thumbnail, title, control }: Props): JSX.Element => {
  return (
    <Styled.Container role="listitem">
      <Styled.ImageWrapper>
        <Styled.Image src={thumbnail.src} alt={thumbnail.alt} loading="lazy" />
      </Styled.ImageWrapper>
      <Styled.TitleWrapper>
        <Styled.Title>{title}</Styled.Title>
        {control && <Styled.Control>{control}</Styled.Control>}
      </Styled.TitleWrapper>
    </Styled.Container>
  );
};

export default SpaceListItem;
