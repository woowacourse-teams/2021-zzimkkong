import React, { useContext } from 'react';

const useFormContext = <T>(context: React.Context<T | null>): T => {
  const contextData = useContext<T | null>(context);

  if (contextData === null) {
    throw new Error('context가 존재하지 않습니다.');
  }

  return contextData;
};

export default useFormContext;
