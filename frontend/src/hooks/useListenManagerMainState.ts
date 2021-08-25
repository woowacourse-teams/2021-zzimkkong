import { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import PATH from 'constants/path';

interface Props {
  mapId?: number | null;
}

const useListenManagerMainState = ({ mapId }: Props): void => {
  const history = useHistory();

  useEffect(
    () =>
      history.listen((location) => {
        if (location.pathname === PATH.MANAGER_MAIN) {
          location.state = { mapId };
        }
      }),
    [history, mapId]
  );
};

export default useListenManagerMainState;
