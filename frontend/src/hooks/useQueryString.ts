import { useLocation } from 'react-router-dom';

const useQueryString = (): URLSearchParams => new URLSearchParams(useLocation().search);

export default useQueryString;
