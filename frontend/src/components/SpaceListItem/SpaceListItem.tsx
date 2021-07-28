import * as Styled from './SpaceListItem.styles';

export interface Props {
  thumbnail: {
    src: string;
    alt: string;
  };
  title: string;
}

const SpaceListItem = ({ thumbnail, title }: Props): JSX.Element => {
  return (
    <Styled.Container role="listitem">
      <Styled.ImageWrapper>
        <Styled.Image src={thumbnail.src} alt={thumbnail.alt} />
      </Styled.ImageWrapper>
      <Styled.Title>{title}</Styled.Title>
    </Styled.Container>
  );
};

export default SpaceListItem;
