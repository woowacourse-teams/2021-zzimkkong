import { useState, ChangeEventHandler } from 'react';

const useToggle = (initialValue = false): [typeof value, ChangeEventHandler<HTMLInputElement>] => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setValue(event.target.checked);
  };

  return [value, onChange];
};

export default useToggle;
