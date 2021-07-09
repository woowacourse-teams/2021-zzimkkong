import { useState, ChangeEventHandler } from 'react';

const useInput = <T extends unknown>(initialValue: T) => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setValue(event.target.value as T);
  };

  return [value, onChange] as const;
};

export default useInput;
