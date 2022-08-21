import { Dispatch, FormEventHandler, SetStateAction, useEffect, useRef, useState } from 'react';
import {
  DeleteManagerSpaceParams,
  PostManagerSpaceParams,
  PutManagerSpaceParams,
} from 'api/managerSpace';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import Button from 'components/Button/Button';
import ColorDot from 'components/ColorDot/ColorDot';
import Input from 'components/Input/Input';
import Toggle from 'components/Toggle/Toggle';
import MESSAGE from 'constants/message';
import useFormContext from 'hooks/useFormContext';
import { Area, Color, ManagerSpace, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from 'types/editor';
import { generateSvg, MapSvgData } from 'utils/generateSvg';
import { colorSelectOptions, timeUnits } from '../data';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import * as Styled from './Form.styles';
import FormDayOfWeekSelect from './FormDayOfWeekSelect';
import FormTimeUnitSelect from './FormTimeUnitSelect';
import Preset from './Preset';

interface Props {
  modeState: [Mode, Dispatch<SetStateAction<Mode>>];
  mapData: { width: number; height: number; mapElements: MapElement[] };
  spaces: ManagerSpace[];
  selectedSpaceId: number | null;
  disabled: boolean;
  onCreateSpace: (data: Omit<PostManagerSpaceParams, 'mapId'>) => void;
  onUpdateSpace: (data: Omit<PutManagerSpaceParams, 'mapId'>) => void;
  onDeleteSpace: (data: Omit<DeleteManagerSpaceParams, 'mapId'>) => void;
}

const Form = ({
  modeState,
  mapData,
  spaces,
  selectedSpaceId,
  disabled,
  onCreateSpace,
  onUpdateSpace,
  onDeleteSpace,
}: Props): JSX.Element => {
  const nameInputRef = useRef<HTMLInputElement | null>(null);

  const [mode, setMode] = modeState;

  const {
    values,
    onChange,
    resetForm,
    setValues,
    getRequestValues,
    selectedSettingIndex,
    onChangeSettingSelectedIndex,
    createSetting,
    removeSetting,
  } = useFormContext(SpaceFormContext);

  const setColor = (color: Color) => {
    setValues({ ...values, color });
  };

  const getSpacesForSvg = (): MapSvgData['spaces'] => {
    if (selectedSpaceId === null && values.area) {
      return [...spaces, { area: values.area, color: values.color }];
    }

    return spaces.map((space) =>
      space.id === selectedSpaceId ? { area: values.area as Area, color: values.color } : space
    );
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const thumbnail = generateSvg({ ...mapData, spaces: getSpacesForSvg() });
    const {
      space: { description, ...rest },
    } = getRequestValues();

    if (selectedSpaceId === null) {
      onCreateSpace({
        space: {
          thumbnail,
          description: description ? description : null,
          ...rest,
        },
      });

      return;
    }

    onUpdateSpace({
      spaceId: selectedSpaceId,
      space: {
        description: description ? description : null,
        ...rest,
        thumbnail,
      },
    });
  };

  const handleDelete = () => {
    if (selectedSpaceId === null) return;
    if (!window.confirm(MESSAGE.MANAGER_SPACE.DELETE_SPACE_CONFIRM)) return;

    const filteredSpaces = spaces.filter(({ id }) => id !== selectedSpaceId);
    const thumbnail = generateSvg({ ...mapData, spaces: filteredSpaces });

    onDeleteSpace({
      spaceId: selectedSpaceId,
      thumbnail,
    });

    resetForm();
  };

  const handleCancel = () => {
    resetForm();

    setMode(Mode.Default);
  };

  useEffect(() => {
    if (mode !== Mode.Form) return;

    nameInputRef.current?.focus();
  }, [mode, selectedSpaceId]);

  return (
    <Styled.Form onSubmit={handleSubmit} disabled={disabled}>
      <Styled.Section>
        <Styled.TitleContainer>
          <Styled.Title>공간 설정</Styled.Title>
          <Toggle
            variant="primary"
            uncheckedText="예약 비활성화"
            checkedText="예약 활성화"
            textPosition="left"
            name="reservationEnable"
            checked={values.reservationEnable}
            onChange={(event) => onChange(event, selectedSettingIndex)}
          />
        </Styled.TitleContainer>

        <Styled.ContentsContainer>
          <Styled.Row>
            <Input
              icon={<ColorDot color={values.color} size="medium" />}
              label="공간 이름"
              value={values.name}
              name="name"
              onChange={(event) => onChange(event, selectedSettingIndex)}
              ref={nameInputRef}
              maxLength={20}
              required
            />
          </Styled.Row>

          <Styled.Row>
            <Styled.ColorSelect>
              <Styled.ColorInputLabel>
                <PaletteIcon />
                <Styled.ColorInput
                  type="color"
                  value={values.color}
                  name="color"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  required
                />
              </Styled.ColorInputLabel>
              {colorSelectOptions.map((color) => (
                <Styled.ColorDotButton key={color} type="button" onClick={() => setColor(color)}>
                  <ColorDot size="large" color={color} />
                </Styled.ColorDotButton>
              ))}
            </Styled.ColorSelect>
          </Styled.Row>
        </Styled.ContentsContainer>
      </Styled.Section>

      <Styled.Section>
        <Styled.TitleContainer>
          <Styled.Title>예약 조건</Styled.Title>
        </Styled.TitleContainer>

        <div>
          {values.settings.map((_, index) => (
            <button key={index} type="button" onClick={() => onChangeSettingSelectedIndex(index)}>
              예약조건 {index + 1}
            </button>
          ))}

          <button type="button" onClick={createSetting}>
            +
          </button>
        </div>
        <div>
          <span>{selectedSettingIndex}</span>
          <Styled.ContentsContainer>
            <Styled.Row>
              <Preset />
            </Styled.Row>

            <Styled.Row>
              <Styled.InputWrapper>
                <Input
                  type="time"
                  label="예약이 열릴 시간"
                  value={values.settings[selectedSettingIndex].settingStartTime}
                  name="settingStartTime"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  required
                />
                <Input
                  type="time"
                  label="예약이 닫힐 시간"
                  value={values.settings[selectedSettingIndex].settingEndTime}
                  name="settingEndTime"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  required
                />
              </Styled.InputWrapper>
              <Styled.InputMessage>
                예약이 열릴 시간과 닫힐 시간을 설정해주세요.
              </Styled.InputMessage>
            </Styled.Row>

            <Styled.Row>
              <Styled.Fieldset>
                <Styled.Label>예약 시간 단위</Styled.Label>
                <FormTimeUnitSelect
                  timeUnits={timeUnits}
                  selectedValue={`${values.settings[selectedSettingIndex].reservationTimeUnit}`}
                  name="reservationTimeUnit"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                />
              </Styled.Fieldset>
              <Styled.InputMessage>예약 시간의 단위를 설정해주세요.</Styled.InputMessage>
            </Styled.Row>

            <Styled.Row>
              <Styled.InputWrapper>
                <Input
                  type="number"
                  min="0"
                  max="1440"
                  step={values.settings[selectedSettingIndex].reservationTimeUnit}
                  label="최소 예약 시간(분)"
                  value={values.settings[selectedSettingIndex].reservationMinimumTimeUnit}
                  name="reservationMinimumTimeUnit"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  required
                />
                <Input
                  type="number"
                  min={values.settings[selectedSettingIndex].reservationMinimumTimeUnit}
                  max="1440"
                  step={values.settings[selectedSettingIndex].reservationTimeUnit}
                  label="최대 예약 시간(분)"
                  value={values.settings[selectedSettingIndex].reservationMaximumTimeUnit}
                  name="reservationMaximumTimeUnit"
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  required
                />
              </Styled.InputWrapper>
              <Styled.InputMessage>
                예약 가능한 최소 시간과 최대 시간을 설정해주세요.
              </Styled.InputMessage>
            </Styled.Row>

            <Styled.Row>
              <Styled.Fieldset>
                <Styled.Label>예약 가능한 요일</Styled.Label>
                <FormDayOfWeekSelect
                  onChange={(event) => onChange(event, selectedSettingIndex)}
                  enabledDayOfWeek={values.settings[selectedSettingIndex].enabledDayOfWeek}
                />
              </Styled.Fieldset>
            </Styled.Row>

            <Styled.Row>
              {selectedSpaceId ? (
                <Styled.FormSubmitContainer>
                  <Styled.DeleteButton type="button" variant="text" onClick={handleDelete}>
                    <DeleteIcon />
                    공간 삭제
                  </Styled.DeleteButton>
                  <Button variant="primary">저장</Button>
                </Styled.FormSubmitContainer>
              ) : (
                <Styled.FormSubmitContainer>
                  <Button type="button" variant="text" onClick={handleCancel}>
                    취소
                  </Button>
                  <Button variant="primary">공간 추가</Button>
                </Styled.FormSubmitContainer>
              )}
            </Styled.Row>
          </Styled.ContentsContainer>
        </div>
      </Styled.Section>
    </Styled.Form>
  );
};

export default Form;
