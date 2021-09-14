import { ChangeEventHandler } from 'react';
import * as Styled from './FormTimeUnitSelect.styles';

interface Props {
  timeUnits: string[];
  name: string;
  selectedValue: string;
  onChange: ChangeEventHandler<HTMLInputElement>;
}

const FormTimeUnitSelect = ({ timeUnits, name, onChange, selectedValue }: Props): JSX.Element => {
  return (
    <Styled.Container>
      {timeUnits.map((timeUnit) => (
        <Styled.Label key={`time-unit-${timeUnit}`}>
          <Styled.Input
            type="radio"
            checked={selectedValue === timeUnit}
            value={timeUnit}
            name={name}
            onChange={onChange}
            required
          />
          <Styled.LabelText>{timeUnit}분</Styled.LabelText>
        </Styled.Label>
      ))}
    </Styled.Container>
  );
};

export default FormTimeUnitSelect;
