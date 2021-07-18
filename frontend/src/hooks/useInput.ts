import { useState, ChangeEventHandler } from 'react';

const useInput = (initialValue = ''): [typeof value, ChangeEventHandler<HTMLInputElement>] => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setValue(event.target.value);
  };

  return [value, onChange];
};

export default useInput;
