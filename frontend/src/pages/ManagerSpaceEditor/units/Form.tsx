import { Dispatch, FormEventHandler, SetStateAction, useEffect, useRef } from 'react';
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

  const { values, onChange, resetForm, setValues, getRequestValues } =
    useFormContext(SpaceFormContext);

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
    const valuesForRequest = getRequestValues();

    if (selectedSpaceId === null) {
      onCreateSpace({
        space: {
          thumbnail,
          ...valuesForRequest.space,
          settingsRequest: { ...valuesForRequest.space.settings },
        },
      });

      return;
    }

    onUpdateSpace({
      spaceId: selectedSpaceId,
      space: {
        thumbnail,
        ...valuesForRequest.space,
        settingsRequest: { ...valuesForRequest.space.settings },
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
          <Styled.Title>?????? ??????</Styled.Title>
          <Toggle
            variant="primary"
            uncheckedText="?????? ????????????"
            checkedText="?????? ?????????"
            textPosition="left"
            name="reservationEnable"
            checked={values.reservationEnable}
            onChange={onChange}
          />
        </Styled.TitleContainer>

        <Styled.ContentsContainer>
          <Styled.Row>
            <Input
              icon={<ColorDot color={values.color} size="medium" />}
              label="?????? ??????"
              value={values.name}
              name="name"
              onChange={onChange}
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
                  onChange={onChange}
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
          <Styled.Title>?????? ??????</Styled.Title>
        </Styled.TitleContainer>

        <Styled.ContentsContainer>
          <Styled.Row>
            <Preset />
          </Styled.Row>

          <Styled.Row>
            <Styled.InputWrapper>
              <Input
                type="time"
                label="????????? ?????? ??????"
                value={values.availableStartTime}
                name="availableStartTime"
                onChange={onChange}
                required
              />
              <Input
                type="time"
                label="????????? ?????? ??????"
                value={values.availableEndTime}
                name="availableEndTime"
                onChange={onChange}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputMessage>????????? ?????? ????????? ?????? ????????? ??????????????????.</Styled.InputMessage>
          </Styled.Row>

          <Styled.Row>
            <Styled.Fieldset>
              <Styled.Label>?????? ?????? ??????</Styled.Label>
              <FormTimeUnitSelect
                timeUnits={timeUnits}
                selectedValue={`${values.reservationTimeUnit}`}
                name="reservationTimeUnit"
                onChange={onChange}
              />
            </Styled.Fieldset>
            <Styled.InputMessage>?????? ????????? ????????? ??????????????????.</Styled.InputMessage>
          </Styled.Row>

          <Styled.Row>
            <Styled.InputWrapper>
              <Input
                type="number"
                min="0"
                max="1440"
                step={values.reservationTimeUnit}
                label="?????? ?????? ??????(???)"
                value={values.reservationMinimumTimeUnit}
                name="reservationMinimumTimeUnit"
                onChange={onChange}
                required
              />
              <Input
                type="number"
                min={values.reservationMinimumTimeUnit}
                max="1440"
                step={values.reservationTimeUnit}
                label="?????? ?????? ??????(???)"
                value={values.reservationMaximumTimeUnit}
                name="reservationMaximumTimeUnit"
                onChange={onChange}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputMessage>
              ?????? ????????? ?????? ????????? ?????? ????????? ??????????????????.
            </Styled.InputMessage>
          </Styled.Row>

          <Styled.Row>
            <Styled.Fieldset>
              <Styled.Label>?????? ????????? ??????</Styled.Label>
              <FormDayOfWeekSelect onChange={onChange} enabledDayOfWeek={values.enabledDayOfWeek} />
            </Styled.Fieldset>
          </Styled.Row>

          <Styled.Row>
            {selectedSpaceId ? (
              <Styled.FormSubmitContainer>
                <Styled.DeleteButton type="button" variant="text" onClick={handleDelete}>
                  <DeleteIcon />
                  ?????? ??????
                </Styled.DeleteButton>
                <Button variant="primary">??????</Button>
              </Styled.FormSubmitContainer>
            ) : (
              <Styled.FormSubmitContainer>
                <Button type="button" variant="text" onClick={handleCancel}>
                  ??????
                </Button>
                <Button variant="primary">?????? ??????</Button>
              </Styled.FormSubmitContainer>
            )}
          </Styled.Row>
        </Styled.ContentsContainer>
      </Styled.Section>
    </Styled.Form>
  );
};

export default Form;
