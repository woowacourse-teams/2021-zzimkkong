import { useEffect, useMemo, useRef } from 'react';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import Input from 'components/Input/Input';
import Toggle from 'components/Toggle/Toggle';
import { Color, ManagerSpace } from 'types/common';
import { colorSelectOptions, SpaceFormValue } from '../data';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import ColorDot from './ColorDot';
import * as Styled from './Form.styles';

interface Props {
  spaces: ManagerSpace[];
  selectedSpaceId: number | null;
  disabled: boolean;
}

const Form = ({ spaces, selectedSpaceId, disabled }: Props): JSX.Element => {
  const nameInputRef = useRef<HTMLInputElement | null>(null);

  const { values, onChange, setValues } = useFormContext(SpaceFormContext);

  const spacesObj = useMemo(() => {
    const result: { [key: string]: ManagerSpace } = {};

    spaces.forEach((space) => (result[space.id] = space));

    return result;
  }, [spaces]);

  const setColor = (color: Color) => {
    setValues({ ...values, color });
  };

  useEffect(() => {
    if (selectedSpaceId === null) return;

    const { name, color, settings } = spacesObj[selectedSpaceId];

    const enableWeekdays = settings.enabledDayOfWeek?.toLowerCase()?.split(',') ?? [];
    const nextEnableWeekdays: { [key: string]: boolean } = {};
    Object.keys(values.enabledWeekdays).forEach(
      (weekday) => (nextEnableWeekdays[weekday] = enableWeekdays.includes(weekday))
    );

    setValues({
      name,
      color,
      ...settings,
      reservationTimeUnit: `${settings.reservationTimeUnit}`,
      reservationMinimumTimeUnit: `${settings.reservationMinimumTimeUnit}`,
      reservationMaximumTimeUnit: `${settings.reservationMaximumTimeUnit}`,
      enabledWeekdays: nextEnableWeekdays as SpaceFormValue['enabledWeekdays'],
    });
  }, [selectedSpaceId, spacesObj]);

  return (
    <Styled.Form disabled={disabled}>
      <Styled.SpaceSettingHeader>
        <Styled.FormHeader>공간 설정</Styled.FormHeader>
        <Toggle
          variant="primary"
          uncheckedText="예약 비활성화"
          checkedText="예약 활성화"
          textPosition="left"
          name="reservationEnable"
          checked={values.reservationEnable}
          onChange={onChange}
        />
      </Styled.SpaceSettingHeader>

      <Styled.FormRow>
        <Input
          icon={<ColorDot color={values.color} size="medium" />}
          label="공간 이름"
          value={values.name}
          name="name"
          onChange={onChange}
          ref={nameInputRef}
          required
        />
      </Styled.FormRow>

      <Styled.FormRow>
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
      </Styled.FormRow>
    </Styled.Form>
  );
};

export default Form;
