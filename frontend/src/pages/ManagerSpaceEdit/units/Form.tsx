import { useEffect, useMemo, useRef } from 'react';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import Input from 'components/Input/Input';
import Toggle from 'components/Toggle/Toggle';
import { Color, ManagerSpace } from 'types/common';
import { colorSelectOptions, SpaceFormValue, timeUnits } from '../data';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import ColorDot from './ColorDot';
import * as Styled from './Form.styles';
import FormTimeUnitSelect from './FormTimeUnitSelect';

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
            onChange={onChange}
          />
        </Styled.TitleContainer>

        <Styled.ContentsContainer>
          <Styled.Row>
            <Input
              icon={<ColorDot color={values.color} size="medium" />}
              label="공간 이름"
              value={values.name}
              name="name"
              onChange={onChange}
              ref={nameInputRef}
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
          <Styled.Title>예약 조건</Styled.Title>
        </Styled.TitleContainer>

        <Styled.ContentsContainer>
          <Styled.Row>
            <Styled.InputWrapper>
              <Input
                type="time"
                label="예약이 열릴 시간"
                value={values.availableStartTime}
                name="availableStartTime"
                onChange={onChange}
                required
              />
              <Input
                type="time"
                label="예약이 닫힐 시간"
                value={values.availableEndTime}
                name="availableEndTime"
                onChange={onChange}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputMessage>
              예약이 열리는 시간과 닫히는 시간을 설정할 수 있습니다.
            </Styled.InputMessage>
          </Styled.Row>

          <Styled.Row>
            <Styled.Fieldset>
              <Styled.Label>예약 시간 단위</Styled.Label>
              <FormTimeUnitSelect
                timeUnits={timeUnits}
                selectedValue={values.reservationTimeUnit}
                onChange={onChange}
              />
            </Styled.Fieldset>
            <Styled.InputMessage>예약할 때의 시간 단위를 설정할 수 있습니다.</Styled.InputMessage>
          </Styled.Row>
        </Styled.ContentsContainer>
      </Styled.Section>
    </Styled.Form>
  );
};

export default Form;
