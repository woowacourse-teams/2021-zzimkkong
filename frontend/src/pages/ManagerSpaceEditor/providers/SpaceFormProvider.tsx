import React, {
  createContext,
  Dispatch,
  ReactNode,
  SetStateAction,
  useEffect,
  useState,
} from 'react';
import { Area, ManagerSpace, ManagerSpaceAPI } from 'types/common';
import { WithOptional } from 'types/util';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';
import {
  initialEnabledDayOfWeek,
  initialSpaceFormValue,
  initialSpaceFormValueSetting,
  SpaceFormValue,
} from '../data';

interface Props {
  children: ReactNode;
}

export interface SpaceProviderValue {
  values: SpaceFormValue;
  onChange: (event: React.ChangeEvent<HTMLInputElement>, settingsIndex: number) => void;
  resetForm: () => void;
  updateArea: (nextArea: Area) => void;
  updateWithSpace: (space: ManagerSpace) => void;
  setValues: (nextValue: SpaceFormValue) => void;
  getRequestValues: () => {
    space: WithOptional<ManagerSpaceAPI, 'id'>;
  };
  selectedPresetId: number | null;
  setSelectedPresetId: Dispatch<SetStateAction<number | null>>;
  selectedSettingIndex: number;
  onChangeSettingSelectedIndex: (settingIndex: number) => void;
  createSetting: () => void;
  removeSetting: (settingIndex: number) => void;
}

export const SpaceFormContext = createContext<SpaceProviderValue | null>(null);
const weekdays = Object.keys(initialEnabledDayOfWeek);
const settingKeys = Object.keys(initialSpaceFormValue.settings[0]);

const SpaceFormProvider = ({ children }: Props): JSX.Element => {
  const [spaceFormValue, setSpaceFormValues] = useState(initialSpaceFormValue);
  const [area, setArea] = useState<Area | null>(null);
  const [selectedPresetId, setSelectedPresetId] = useState<number | null>(null);
  const [selectedSettingIndex, setSelectedSettingIndex] = useState<number>(0);
  const [isCreatingSetting, setIsCreatingSetting] = useState<boolean>(false);

  const values = { ...spaceFormValue, area };

  const setValues = (values: SpaceFormValue) => {
    const { area, ...rest } = values;

    setArea(area);
    setSpaceFormValues(rest);
  };

  const createSetting = () => {
    setSpaceFormValues({
      ...spaceFormValue,
      settings: [...spaceFormValue.settings, initialSpaceFormValueSetting],
    });
    setIsCreatingSetting(true);
  };

  const removeSetting = (settingIndex: number) => {
    if (spaceFormValue.settings.length === 1) {
      return;
    }

    if (selectedSettingIndex === settingIndex) {
      setSelectedSettingIndex(0);
    }

    if (settingIndex < selectedSettingIndex) {
      setSelectedSettingIndex((prev) => prev - 1);
    }

    setSpaceFormValues({
      ...spaceFormValue,
      settings: [
        ...spaceFormValue.settings.slice(0, settingIndex),
        ...spaceFormValue.settings.slice(settingIndex + 1),
      ],
    });
  };

  const onChangeSettingSelectedIndex = (settingIndex: number) => {
    setSelectedSettingIndex(settingIndex);
    setSelectedPresetId(null);
  };

  const updateWithSpace = (space: ManagerSpace) => {
    onChangeSettingSelectedIndex(0);
    setSelectedPresetId(null);
    setValues(space);
  };

  const updateArea = (nextArea: Area) => {
    setArea(nextArea);
    setSpaceFormValues(initialSpaceFormValue);
  };

  const getRequestValues = () => {
    const todayDate = formatDate(new Date());

    for (let i = 0; i < values.settings.length; i++) {
      const setting = values.settings[i];
      setting['priorityOrder'] = values.settings.length - (i + 1);
    }

    return {
      space: {
        ...values,
        area: JSON.stringify(values.area),
        settings: values.settings?.map((setting) => ({
          ...setting,
          settingStartTime: formatTimeWithSecond(
            new Date(`${todayDate}T${setting.settingStartTime}`)
          ),
          settingEndTime: formatTimeWithSecond(new Date(`${todayDate}T${setting.settingEndTime}`)),
        })),
      },
    };
  };

  const onChangeReservationTimeUnit = (
    event: React.ChangeEvent<HTMLInputElement>,
    settingsIndex: number
  ) => {
    const isValidMinimumTimeUnit =
      Number(event.target.value) <=
      spaceFormValue.settings[settingsIndex].reservationMinimumTimeUnit;

    setSpaceFormValues({
      ...spaceFormValue,
      settings: spaceFormValue.settings.map((setting, index) => {
        if (index === settingsIndex) {
          return {
            ...setting,
            reservationTimeUnit: Number(event.target.value),
            reservationMinimumTimeUnit: !isValidMinimumTimeUnit
              ? Number(event.target.value)
              : setting.reservationMinimumTimeUnit,
          };
        }

        return setting;
      }),
    });

    return;
  };

  const onChange = (event: React.ChangeEvent<HTMLInputElement>, settingsIndex: number) => {
    if (selectedPresetId !== null) setSelectedPresetId(null);

    if (weekdays.includes(event.target.name)) {
      setSpaceFormValues({
        ...spaceFormValue,
        settings: spaceFormValue.settings.map((setting, index) => {
          if (index === settingsIndex) {
            return {
              ...setting,
              enabledDayOfWeek: {
                ...setting.enabledDayOfWeek,
                [event.target.name]: event.target.checked,
              },
            };
          }

          return setting;
        }),
      });

      return;
    }

    if (event.target.name === 'reservationTimeUnit') {
      onChangeReservationTimeUnit(event, settingsIndex);

      return;
    }

    if (settingKeys.includes(event.target.name)) {
      setSpaceFormValues({
        ...spaceFormValue,
        settings: spaceFormValue.settings.map((setting, index) => {
          if (index === settingsIndex) {
            return {
              ...setting,

              [event.target.name]: event.target.value,
            };
          }

          return setting;
        }),
      });

      return;
    }

    setSpaceFormValues({
      ...spaceFormValue,
      [event.target.name]: event.target.value,
    });
  };

  const resetForm = () => {
    setSelectedPresetId(null);
    setValues({ ...initialSpaceFormValue, area: null });
  };

  useEffect(() => {
    if (isCreatingSetting) {
      onChangeSettingSelectedIndex(spaceFormValue.settings.length - 1);
      setIsCreatingSetting(false);
    }
  }, [isCreatingSetting, spaceFormValue.settings.length]);

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
        selectedSettingIndex,
        onChangeSettingSelectedIndex,
        createSetting,
        removeSetting,
      }}
    >
      {children}
    </SpaceFormContext.Provider>
  );
};

export default SpaceFormProvider;
