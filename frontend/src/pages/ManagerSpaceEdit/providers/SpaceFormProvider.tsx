import React, { createContext, ReactNode } from 'react';
import useInputs from 'hooks/useInputs';
import { initialEnabledWeekdays, initialSpaceFormValue, SpaceFormValue } from '../data';

interface Props {
  children: ReactNode;
}

interface SpaceFormValueOptionalEnabledWeekdays extends Omit<SpaceFormValue, 'enabledWeekdays'> {
  enabledWeekdays?: SpaceFormValue['enabledWeekdays'];
}

export interface SpaceProviderValue {
  values: SpaceFormValue;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  setValues: (nextValue: SpaceFormValue) => void;
}

export const SpaceFormContext = createContext<SpaceProviderValue | null>(null);
const weekdays = Object.keys(initialEnabledWeekdays);

const SpaceFormProvider = ({ children }: Props): JSX.Element => {
  const [spaceFormValue, onChangeSpaceFormValues, setSpaceFormValues] =
    useInputs(initialSpaceFormValue);
  const [enabledWeekdays, onChangeEnabledWeekdays, setEnabledWeekdays] =
    useInputs(initialEnabledWeekdays);

  const values = { ...spaceFormValue, enabledWeekdays };

  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (weekdays.includes(event.target.name)) {
      onChangeEnabledWeekdays(event);

      return;
    }

    onChangeSpaceFormValues(event);
  };

  const setValues = (nextValues: SpaceFormValue) => {
    setEnabledWeekdays({ ...nextValues.enabledWeekdays });

    delete (nextValues as SpaceFormValueOptionalEnabledWeekdays).enabledWeekdays;

    setSpaceFormValues(nextValues);
  };

  return (
    <SpaceFormContext.Provider value={{ values, onChange, setValues }}>
      {children}
    </SpaceFormContext.Provider>
  );
};

export default SpaceFormProvider;
