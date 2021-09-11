import { ChangeEventHandler } from 'react';
import * as Styled from './FormWeekdaySelect.styles';

interface Props {
  onChange: ChangeEventHandler<HTMLInputElement>;
  enabledWeekdays: { [key: string]: boolean };
}

interface Weekday<T> {
  displayName: string;
  inputName: T;
}

const weekday: { [key in keyof Props['enabledWeekdays']]: Weekday<key> } = {
  monday: {
    displayName: '월',
    inputName: 'monday',
  },
  tuesday: {
    displayName: '화',
    inputName: 'tuesday',
  },
  wednesday: {
    displayName: '수',
    inputName: 'wednesday',
  },
  thursday: {
    displayName: '목',
    inputName: 'thursday',
  },
  friday: {
    displayName: '금',
    inputName: 'friday',
  },
  saturday: {
    displayName: '토',
    inputName: 'saturday',
  },
  sunday: {
    displayName: '일',
    inputName: 'sunday',
  },
};

const displayOrder = [
  weekday.monday,
  weekday.tuesday,
  weekday.wednesday,
  weekday.thursday,
  weekday.friday,
  weekday.saturday,
  weekday.sunday,
];

const FormWeekdaySelect = ({ onChange, enabledWeekdays }: Props): JSX.Element => {
  return (
    <Styled.Container>
      {displayOrder.map(({ displayName, inputName }) => (
        <Styled.Label key={`weekday-select-${inputName}`}>
          <Styled.DisplayName>{displayName}</Styled.DisplayName>
          <input
            type="checkbox"
            checked={enabledWeekdays[inputName]}
            name={inputName}
            onChange={onChange}
          />
        </Styled.Label>
      ))}
    </Styled.Container>
  );
};

export default FormWeekdaySelect;
