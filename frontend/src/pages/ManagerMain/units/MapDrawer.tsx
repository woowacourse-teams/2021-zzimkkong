import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import Drawer from 'components/Drawer/Drawer';
import IconButton from 'components/IconButton/IconButton';
import MapListItem from 'components/MapListItem/MapListItem';
import PATH from 'constants/path';
import { MapItemResponse } from 'types/response';
import * as Styled from './MapDrawer.styles';

interface Props {
  selectedMapId: number;
  organization: string;
  maps: MapItemResponse[];
  open: boolean;
  onCloseDrawer: () => void;
  onSelectMap: (mapId: number, mapName: string) => void;
  onDeleteMap: (mapId: number) => void;
}

const MapDrawer = ({
  selectedMapId,
  organization,
  maps,
  open,
  onCloseDrawer,
  onSelectMap,
  onDeleteMap,
}: Props): JSX.Element => {
  return (
    <Drawer open={open} placement="left" maxwidth="450px" onClose={onCloseDrawer}>
      <Drawer.Inner>
        <Drawer.Header>
          <Drawer.HeaderText>{organization}</Drawer.HeaderText>
          <Drawer.CloseButton />
        </Drawer.Header>
        {maps.map(({ mapId, mapName, thumbnail }) => (
          <Styled.SpaceWrapper key={`map-${mapId}`}>
            <MapListItem
              onClick={() => onSelectMap(mapId, mapName)}
              thumbnail={thumbnail}
              title={mapName}
              selected={mapId === selectedMapId}
              control={
                <IconButton size="small" onClick={() => onDeleteMap(mapId)}>
                  <DeleteIcon width="100%" height="100%" />
                </IconButton>
              }
            />
          </Styled.SpaceWrapper>
        ))}

        <Styled.CreateMapButton to={PATH.MANAGER_MAP_CREATE}>
          <Styled.PlusIcon width="100%" height="100%" />
        </Styled.CreateMapButton>
      </Drawer.Inner>
    </Drawer>
  );
};

export default MapDrawer;
