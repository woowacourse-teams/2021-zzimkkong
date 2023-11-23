import { AxiosError } from 'axios';
import React from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { deleteMapV2 } from 'api-v2/managerMap';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import SlackNotiButton from 'components/ManagerIconButtons/SlackNotiButton';
import MapListItem from 'components/MapListItem/MapListItem';
import TabLayout from 'components/TabLayout/TabLayout';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import { TAB_LABEL, TAB_LIST, TAB_PATH_FOR_LABEL } from 'constants/tab';
import useManagerMapsV2 from 'hooks/query-v2/useManagerMapsV2';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerMapList.styles';

const ManagerMapList = (): JSX.Element => {
  const history = useHistory();

  const { data: maps } = useManagerMapsV2({
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
    },
  });

  const removeMap = useMutation(deleteMapV2, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.MAP_DELETED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_MAP_DELETE_ERROR);
    },
  });

  const handleMapRemove = (mapId: number) => {
    removeMap.mutate({ mapId });
  };

  return (
    <>
      <Header />
      <TabLayout
        tabList={TAB_LIST}
        defaultTabLabel={TAB_LABEL.MANAGER}
        onClick={(selectedTab) => history.push(TAB_PATH_FOR_LABEL[selectedTab])}
      >
        <Styled.MapListContainer>
          <Styled.MapListTitle>맵 리스트</Styled.MapListTitle>

          <Styled.MapList role="list">
            {maps?.data.maps.map((map) => (
              <MapListItem
                key={map.mapId}
                map={map}
                onClick={() => history.push(HREF.MANAGER_MAP_DETAIL(map.mapId))}
                control={
                  <>
                    {/* <SlackNotiButton map={map} /> */}
                    <IconButton onClick={() => handleMapRemove(map.mapId)}>
                      <DeleteIcon width="24" height="24" />
                    </IconButton>
                  </>
                }
              />
            ))}
          </Styled.MapList>

          <Styled.ButtonContainer>
            <Styled.RoundedButton
              variant="default"
              shape="round"
              onClick={() => history.push(PATH.MANAGER_MAP_CREATE)}
            >
              맵 만들기
            </Styled.RoundedButton>
          </Styled.ButtonContainer>
        </Styled.MapListContainer>
      </TabLayout>
    </>
  );
};

export default ManagerMapList;
