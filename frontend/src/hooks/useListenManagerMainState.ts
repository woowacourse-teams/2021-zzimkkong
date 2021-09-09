import { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import PATH from 'constants/path';
import { isNullish } from 'utils/type';

interface Props {
  mapId?: number;
}

const useListenManagerMainState = (
  { mapId }: Props,
  { enabled }: { enabled: boolean } = { enabled: true }
): void => {
  const history = useHistory<Props>();

  useEffect(() => {
    if (!enabled || isNullish(mapId)) return;

    history.listen((location) => {
      if (location.pathname === PATH.MANAGER_MAIN) {
        location.state = { mapId };
      }
    });
  }, [history, mapId, enabled]);
};

export default useListenManagerMainState;
