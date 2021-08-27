import { useState, ChangeEventHandler, Dispatch, SetStateAction } from 'react';

const useToggle = (
  initialValue = false
): [typeof value, ChangeEventHandler<HTMLInputElement>, Dispatch<SetStateAction<typeof value>>] => {
  const [value, setValue] = useState(initialValue);

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setValue(event.target.checked);
  };

  return [value, onChange, setValue];
};

export default useToggle;
