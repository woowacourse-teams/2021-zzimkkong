import { PropsWithChildren } from 'react';
import { Redirect, Route, RouteProps } from 'react-router-dom';
import { useRecoilValue } from 'recoil';
import accessTokenState from 'state/accessTokenState';

interface Props extends RouteProps {
  redirectPath: string;
}

const PrivateRoute = ({
  redirectPath,
  children,
  ...props
}: PropsWithChildren<Props>): JSX.Element => {
  const token = useRecoilValue(accessTokenState);

  return <Route {...props}>{token ? children : <Redirect to={redirectPath} />}</Route>;
};

export default PrivateRoute;
