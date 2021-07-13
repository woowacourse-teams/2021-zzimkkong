import { useState, ChangeEventHandler } from 'react';

const useInput = <T extends unknown>(
  initialValue: T
): [T, ChangeEventHandler<HTMLInputElement>] => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setValue(event.target.value as T);
  };

  return [value, onChange];
};

export default useInput;
