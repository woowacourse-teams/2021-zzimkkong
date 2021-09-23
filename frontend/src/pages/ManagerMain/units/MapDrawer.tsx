import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
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
  onClose: () => void;
  onSelectMap: (mapId: number, mapName: string) => void;
  onDeleteMap: (mapId: number) => void;
}

const MapDrawer = ({
  selectedMapId,
  open,
  onClose,
  maps,
  organization,
  onSelectMap,
  onDeleteMap,
}: Props): JSX.Element => {
  return (
    <Drawer open={open} placement="left" maxwidth="450px" onClose={onClose}>
      <Drawer.Inner>
        <Drawer.Header>
          <Drawer.HeaderText>{organization}</Drawer.HeaderText>
          <Drawer.CloseButton />
        </Drawer.Header>
        {maps.map(({ mapId, mapName, mapImageUrl }) => (
          <Styled.SpaceWrapper key={`map-${mapId}`}>
            <MapListItem
              onClick={() => onSelectMap(mapId, mapName)}
              thumbnail={{ src: mapImageUrl, alt: mapName }}
              title={mapName}
              selected={mapId === selectedMapId}
              control={
                <>
                  <IconButton size="small">
                    <EditIcon width="100%" height="100%" />
                  </IconButton>
                  <IconButton size="small" onClick={() => onDeleteMap(mapId)}>
                    <DeleteIcon width="100%" height="100%" />
                  </IconButton>
                </>
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
