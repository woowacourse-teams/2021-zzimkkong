import React, { createContext, Dispatch, ReactNode, SetStateAction, useState } from 'react';
import useInputs from 'hooks/useInputs';
import { Area } from 'types/common';
import { WithOptional } from 'types/util';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';
import { initialEnabledWeekdays, initialSpaceFormValue, SpaceFormValue } from '../data';

interface Props {
  children: ReactNode;
}

export interface SpaceProviderValue {
  values: SpaceFormValue;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onCancel: () => void;
  setValues: (nextValue: SpaceFormValue) => void;
  setArea: (nextArea: Area) => void;
  getRequestValues: () => {
    space: {
      name: string;
      color: string;
      description: string;
      area: string;
      settingsRequest: {
        availableStartTime: string;
        availableEndTime: string;
        reservationTimeUnit: number;
        reservationMinimumTimeUnit: number;
        reservationMaximumTimeUnit: number;
        reservationEnable: boolean;
        enabledDayOfWeek: string;
      };
    };
  };
}

export const SpaceFormContext = createContext<SpaceProviderValue | null>(null);
const weekdays = Object.keys(initialEnabledWeekdays);

const SpaceFormProvider = ({ children }: Props): JSX.Element => {
  const [spaceFormValue, onChangeSpaceFormValues, setSpaceFormValues] =
    useInputs(initialSpaceFormValue);
  const [enabledWeekdays, onChangeEnabledWeekdays, setEnabledWeekdays] =
    useInputs(initialEnabledWeekdays);
  const [area, setArea] = useState<Area | null>(null);

  const values = { ...spaceFormValue, enabledWeekdays, area };

  const setValues = (nextValues: SpaceFormValue) => {
    setEnabledWeekdays({ ...nextValues.enabledWeekdays });
    setArea(nextValues.area === null ? null : { ...nextValues.area });

    delete (nextValues as WithOptional<SpaceFormValue, 'enabledWeekdays'>).enabledWeekdays;
    delete (nextValues as WithOptional<SpaceFormValue, 'area'>).area;

    setSpaceFormValues(nextValues);
  };

  const updateArea = (nextArea: Area) => {
    setArea(nextArea);
    setSpaceFormValues(initialSpaceFormValue);
    setEnabledWeekdays(initialEnabledWeekdays);
  };

  const getRequestValues = () => {
    const todayDate = formatDate(new Date());

    const availableStartTime = formatTimeWithSecond(
      new Date(`${todayDate}T${values.availableStartTime}`)
    );
    const availableEndTime = formatTimeWithSecond(
      new Date(`${todayDate}T${values.availableEndTime}`)
    );

    const enabledDayOfWeek = Object.keys(values.enabledWeekdays)
      .filter(
        (weekday) => values.enabledWeekdays[weekday as keyof SpaceFormValue['enabledWeekdays']]
      )
      .join();

    return {
      space: {
        name: values.name,
        color: values.color,
        description: values.name,
        area: JSON.stringify(values.area),
        settingsRequest: {
          availableStartTime,
          availableEndTime,
          reservationTimeUnit: Number(values.reservationTimeUnit),
          reservationMinimumTimeUnit: Number(values.reservationMinimumTimeUnit),
          reservationMaximumTimeUnit: Number(values.reservationMaximumTimeUnit),
          reservationEnable: values.reservationEnable,
          enabledDayOfWeek,
        },
      },
    };
  };

  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (weekdays.includes(event.target.name)) {
      onChangeEnabledWeekdays(event);

      return;
    }

    onChangeSpaceFormValues(event);
  };

  const onCancel = () => {
    setValues({ ...initialSpaceFormValue, enabledWeekdays: initialEnabledWeekdays, area: null });
  };

  return (
    <SpaceFormContext.Provider
      value={{ values, onChange, onCancel, setValues, setArea: updateArea, getRequestValues }}
    >
      {children}
    </SpaceFormContext.Provider>
  );
};

export default SpaceFormProvider;
