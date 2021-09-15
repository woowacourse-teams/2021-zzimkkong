import React, { useContext } from 'react';
import THROW_ERROR from 'constants/throwError';

const useFormContext = <T>(context: React.Context<T | null>): T => {
  const contextData = useContext<T | null>(context);

  if (contextData === null) {
    throw new Error(THROW_ERROR.NOT_EXIST_CONTEXT);
  }

  return contextData;
};

export default useFormContext;
