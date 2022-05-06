import * as Styled from './TextArea.styles';

export interface Props extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  value?: string;
}

const TextArea = ({ label, value, ...props }: Props): JSX.Element => {
  return (
    <Styled.Label hasLabel={!!label}>
      {label && <Styled.LabelText>{label}</Styled.LabelText>}
      <Styled.TextArea {...props} value={value} />
      <Styled.TextLength>
        {value?.length ?? 0}
        {props.maxLength ? ` / ${props.maxLength}` : ''}
      </Styled.TextLength>
    </Styled.Label>
  );
};

export default TextArea;
