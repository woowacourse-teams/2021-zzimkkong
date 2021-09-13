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
import * as Styled from './Preset.styles';

interface CreateResponseHeaders {
  location: string;
}

const Preset = (): JSX.Element => {
  const [selectedPresetId, setSelectedPresetId] = useState<number | null>(null);

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

  const handleSelectPreset = (id: number) => {
    //
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
        <IconButton type="button" onClick={handleDeletePreset}>
          <DeleteIcon />
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
