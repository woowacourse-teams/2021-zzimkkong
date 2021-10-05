import React, { createContext, Dispatch, ReactNode, SetStateAction, useState } from 'react';
import useInputs from 'hooks/useInputs';
import { Area, ManagerSpace, ManagerSpaceAPI } from 'types/common';
import { WithOptional } from 'types/util';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';
import { initialEnabledDayOfWeek, initialSpaceFormValue, SpaceFormValue } from '../data';

interface Props {
  children: ReactNode;
}

export interface SpaceProviderValue {
  values: SpaceFormValue;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  resetForm: () => void;
  updateArea: (nextArea: Area) => void;
  updateWithSpace: (space: ManagerSpace) => void;
  setValues: (nextValue: SpaceFormValue) => void;
  getRequestValues: () => {
    space: WithOptional<ManagerSpaceAPI, 'id'>;
  };
  selectedPresetId: number | null;
  setSelectedPresetId: Dispatch<SetStateAction<number | null>>;
}

export const SpaceFormContext = createContext<SpaceProviderValue | null>(null);
const weekdays = Object.keys(initialEnabledDayOfWeek);

const SpaceFormProvider = ({ children }: Props): JSX.Element => {
  const [spaceFormValue, onChangeSpaceFormValues, setSpaceFormValues] =
    useInputs(initialSpaceFormValue);
  const [enabledDayOfWeek, onChangeEnabledDayOfWeek, setEnabledDayOfWeek] =
    useInputs(initialEnabledDayOfWeek);
  const [area, setArea] = useState<Area | null>(null);
  const [selectedPresetId, setSelectedPresetId] = useState<number | null>(null);

  const values = { ...spaceFormValue, enabledDayOfWeek, area };

  const setValues = (values: SpaceFormValue) => {
    setEnabledDayOfWeek({ ...values.enabledDayOfWeek });
    setArea(values.area === null ? null : { ...values.area });

    const nextValues = { ...values };

    delete (nextValues as WithOptional<SpaceFormValue, 'enabledDayOfWeek'>).enabledDayOfWeek;
    delete (nextValues as WithOptional<SpaceFormValue, 'area'>).area;

    setSpaceFormValues(nextValues);
  };

  const updateWithSpace = (space: ManagerSpace) => {
    const { name, color, area, settings } = space;

    setSelectedPresetId(null);
    setValues({
      name,
      color,
      ...settings,
      enabledDayOfWeek: settings.enabledDayOfWeek,
      area,
    });
  };

  const updateArea = (nextArea: Area) => {
    setArea(nextArea);
    setSpaceFormValues(initialSpaceFormValue);
    setEnabledDayOfWeek(initialEnabledDayOfWeek);
  };

  const getRequestValues = () => {
    const todayDate = formatDate(new Date());

    const availableStartTime = formatTimeWithSecond(
      new Date(`${todayDate}T${values.availableStartTime}`)
    );
    const availableEndTime = formatTimeWithSecond(
      new Date(`${todayDate}T${values.availableEndTime}`)
    );

    return {
      space: {
        name: values.name,
        color: values.color,
        description: values.name,
        area: JSON.stringify(values.area),
        settings: {
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
    if (selectedPresetId !== null) setSelectedPresetId(null);

    if (weekdays.includes(event.target.name)) {
      onChangeEnabledDayOfWeek(event);

      return;
    }

    onChangeSpaceFormValues(event);
  };

  const resetForm = () => {
    setSelectedPresetId(null);
    setValues({ ...initialSpaceFormValue, enabledDayOfWeek: initialEnabledDayOfWeek, area: null });
  };

  return (
    <SpaceFormContext.Provider
      value={{
        values,
        onChange,
        resetForm,
        updateArea,
        setValues,
        updateWithSpace,
        getRequestValues,
        selectedPresetId,
        setSelectedPresetId,
      }}
    >
      {children}
    </SpaceFormContext.Provider>
  );
};

export default SpaceFormProvider;
