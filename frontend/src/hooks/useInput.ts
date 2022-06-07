import { useState, ChangeEventHandler, SetStateAction, Dispatch } from 'react';

const useInput = (
  initialValue = ''
): [
  typeof value,
  ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement>,
  Dispatch<SetStateAction<typeof value>>
] => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement> = (event) => {
    setValue(event.target.value);
  };

  return [value, onChange, setValue];
};

export default useInput;
