import { PropsWithChildren } from 'react';
import { Redirect, Route, RouteProps } from 'react-router-dom';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import { getLocalStorageItem } from 'utils/localStorage';

interface Props extends RouteProps {
  redirectPath: string;
}

const PrivateRoute = ({
  redirectPath,
  children,
  ...props
}: PropsWithChildren<Props>): JSX.Element => {
  const token = getLocalStorageItem({
    key: LOCAL_STORAGE_KEY.ACCESS_TOKEN,
    defaultValue: '',
  });

  return <Route {...props}>{token ? children : <Redirect to={redirectPath} />}</Route>;
};

export default PrivateRoute;
