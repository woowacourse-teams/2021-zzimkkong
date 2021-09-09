import React, { useState } from 'react';

const useInputs = <T = Record<string, string>>(
  initialValues: T
): [
  T,
  (event: React.ChangeEvent<HTMLInputElement>) => void,
  React.Dispatch<React.SetStateAction<T>>
] => {
  const [values, setValues] = useState<T>(initialValues);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setValues((prevValues) => ({
      ...prevValues,
      [event.target.name]: event.target.value,
    }));
  };

  return [values, handleChange, setValues];
};

export default useInputs;
