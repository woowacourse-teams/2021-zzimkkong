import { AxiosError } from 'axios';
import React from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { theme } from 'App.styles';
import { deleteMap } from 'api/managerMap';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import { ReactComponent as NoticeIcon } from 'assets/svg/notice.svg';
import { ReactComponent as SlackIcon } from 'assets/svg/slack.svg';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import MapNoticeButton from 'components/ManagerIconButtons/MapNoticeButton';
import ShareLinkButton from 'components/ManagerIconButtons/ShareLinkButton';
import SlackNotiButton from 'components/ManagerIconButtons/SlackNotiButton';
import MapListItem from 'components/MapListItem/MapListItem';
import MemberInfo from 'components/MemberInfo/MemberInfo';
import TabLayout from 'components/TabLayout/TabLayout';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import { TAB_LABEL, TAB_LIST } from 'constants/tab';
import useManagerMaps from 'hooks/query/useManagerMaps';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerMapList.styles';

const ManagerMapList = (): JSX.Element => {
  const history = useHistory();

  const { data: maps } = useManagerMaps({
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
    },
  });

  const removeMap = useMutation(deleteMap, {
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
      <TabLayout tabList={TAB_LIST} defaultTabLabel={TAB_LABEL.MANAGER}>
        <MemberInfo />

        <Styled.MapListContainer>
          <Styled.MapListTitle>나의 맵</Styled.MapListTitle>

          <Styled.MapList role="list">
            {maps?.data.maps.map((map) => (
              <MapListItem
                key={map.mapId}
                map={map}
                onClick={() => history.push(HREF.MANAGER_MAP_DETAIL(map.mapId))}
                control={
                  <>
                    <ShareLinkButton map={map} />
                    <MapNoticeButton map={map} />
                    <SlackNotiButton map={map} />
                    <IconButton onClick={() => handleMapRemove(map.mapId)}>
                      <DeleteIcon width="24" height="24" />
                    </IconButton>
                  </>
                }
              />
            ))}
          </Styled.MapList>
        </Styled.MapListContainer>
      </TabLayout>
    </>
  );
};

export default ManagerMapList;
