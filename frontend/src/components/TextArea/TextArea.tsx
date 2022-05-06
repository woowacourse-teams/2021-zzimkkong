import React, { useRef, useState } from 'react';
import * as Styled from './TextArea.styles';

export interface Props extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  value?: string;
}

const TextArea = ({ label, value, ...props }: Props): JSX.Element => {
  const ref = useRef<HTMLTextAreaElement>(null);
  const [valueLength, setValueLength] = useState(0);

  const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setValueLength(ref.current?.value.length ?? 0);
    props.onChange?.(event);
  };

  return (
    <Styled.Label hasLabel={!!label}>
      {label && <Styled.LabelText>{label}</Styled.LabelText>}
      <Styled.TextArea {...props} ref={ref} value={value} onChange={handleChange} />
      <Styled.TextLength>
        {value?.length ?? valueLength}
        {props.maxLength ? ` / ${props.maxLength}` : ''}
      </Styled.TextLength>
    </Styled.Label>
  );
};

export default TextArea;
