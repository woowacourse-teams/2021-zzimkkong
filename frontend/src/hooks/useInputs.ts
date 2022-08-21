import React, { useState } from 'react';

const useInputs = <T = Record<string, string | boolean | number>>(
  initialValues: T
): [
  T,
  (event: React.ChangeEvent<HTMLInputElement>) => void,
  React.Dispatch<React.SetStateAction<T>>
] => {
  const [values, setValues] = useState<T>(initialValues);

  const getValue = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.type === 'checkbox') {
      return event.target.checked;
    }

    return event.target.value;
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setValues((prevValues) => ({
      ...prevValues,
      [event.target.name]: getValue(event),
    }));
  };

  return [values, handleChange, setValues];
};

export default useInputs;
