import { AxiosError } from 'axios';
import { useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { deletePreset, postPreset } from 'api/presets';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import Select from 'components/Select/Select';
import MESSAGE from 'constants/message';
import usePresets from 'hooks/usePreset';
import { ErrorResponse } from 'types/response';
import { SpaceFormValue } from '../data';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import * as Styled from './Preset.styles';

interface CreateResponseHeaders {
  location: string;
}

const Preset = (): JSX.Element => {
  const { values, setValues, selectedPresetId, setSelectedPresetId } =
    useFormContext(SpaceFormContext);

  const getPresets = usePresets();
  const presets = useMemo(
    () => getPresets.data?.data?.presets ?? [],
    [getPresets.data?.data?.presets]
  );

  const createPreset = useMutation(postPreset, {
    onSuccess: (response) => {
      const { location } = response.headers as CreateResponseHeaders;
      const newPresetId = Number(location.split('/').pop() ?? '');

      setSelectedPresetId(newPresetId);
      // setPresetFormOpen(false);
      // setPresetName('');

      getPresets.refetch();
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.ADD_PRESET_UNEXPECTED_ERROR);
    },
  });

  const removePreset = useMutation(deletePreset, {
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.DELETE_PRESET_UNEXPECTED_ERROR);
    },
  });

  const handleSelectPreset = (id: number | null) => {
    setSelectedPresetId(id);

    if (id === null) return;

    const selectedPreset = presets.find((preset) => preset.id === id) ?? null;

    if (selectedPreset === null) throw new Error(MESSAGE.MANAGER_SPACE.FIND_PRESET_ERROR);

    const {
      availableStartTime,
      availableEndTime,
      reservationTimeUnit,
      reservationMinimumTimeUnit,
      reservationMaximumTimeUnit,
    } = selectedPreset;

    const enabledDayOfWeek = selectedPreset.enabledDayOfWeek?.split(',') ?? [];
    const enabledWeekdays: { [key: string]: boolean } = {};
    Object.keys(values.enabledWeekdays).forEach(
      (weekday) => (enabledWeekdays[weekday] = enabledDayOfWeek?.includes(weekday))
    );

    setValues({
      ...values,
      availableStartTime,
      availableEndTime,
      reservationTimeUnit,
      reservationMinimumTimeUnit,
      reservationMaximumTimeUnit,
      enabledWeekdays: enabledWeekdays as SpaceFormValue['enabledWeekdays'],
    });
  };

  const handleAddPreset = () => {
    //
  };

  const handleDeletePreset = () => {
    //
  };

  const presetOptions = presets.map(({ id, name }) => ({
    value: `${id}`,
    title: name,
    children: (
      <Styled.PresetOption>
        <Styled.PresetName>{name}</Styled.PresetName>
        <IconButton type="button" size="small" onClick={handleDeletePreset}>
          <DeleteIcon width="100%" height="100%" />
        </IconButton>
      </Styled.PresetOption>
    ),
  }));

  return (
    <Styled.PresetSelect>
      <Styled.PresetSelectWrapper>
        <Select
          name="preset"
          label="프리셋 선택"
          options={presetOptions}
          value={`${selectedPresetId ?? ''}`}
          onChange={(id) => handleSelectPreset(Number(id))}
        />
      </Styled.PresetSelectWrapper>
      <Button type="button" onClick={handleAddPreset}>
        추가
      </Button>
    </Styled.PresetSelect>
  );
};

export default Preset;

const data = {
  presets: [
    {
      availableStartTime: '07:00:00',
      availableEndTime: '23:00:00',
      reservationTimeUnit: 10,
      reservationMinimumTimeUnit: 10,
      reservationMaximumTimeUnit: 1440,
      reservationEnable: true,
      enabledDayOfWeek: 'monday,tuesday,wednesday,thursday,friday,saturday,sunday',
      id: 1,
      name: '와아',
    },
    {
      availableStartTime: '07:00:00',
      availableEndTime: '23:00:00',
      reservationTimeUnit: 5,
      reservationMinimumTimeUnit: 10,
      reservationMaximumTimeUnit: 1440,
      reservationEnable: true,
      enabledDayOfWeek: 'monday,tuesday,wednesday,thursday,friday',
      id: 2,
      name: '주말ㄴㄴ',
    },
  ],
};
