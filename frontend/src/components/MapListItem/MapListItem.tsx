import { ReactNode } from 'react';
import { theme } from 'App.styles';
import { ReactComponent as CaretIcon } from 'assets/svg/caret-right.svg';
import { MapItemResponse } from 'types/response';
import * as Styled from './MapListItem.styles';

export interface Props {
  map: MapItemResponse;
  control?: ReactNode;
  onClick?: () => void;
}

const MapListItem = ({ map, control, onClick = () => undefined }: Props): JSX.Element => {
  return (
    <Styled.Container role="listitem">
      <Styled.MapInfo onClick={onClick}>
        <Styled.ImageWrapper>
          <Styled.ImageInner dangerouslySetInnerHTML={{ __html: `${map.thumbnail}` }} />
        </Styled.ImageWrapper>
        <Styled.TitleWrapper>
          <Styled.Title>{map.mapName}</Styled.Title>
          <CaretIcon width={36} height={36} fill={theme.gray[400]} />
        </Styled.TitleWrapper>
      </Styled.MapInfo>
      {control && <Styled.ControlWrapper>{control}</Styled.ControlWrapper>}
    </Styled.Container>
  );
};

export default MapListItem;
