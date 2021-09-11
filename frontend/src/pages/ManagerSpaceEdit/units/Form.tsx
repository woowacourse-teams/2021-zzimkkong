import { FormEventHandler, useEffect, useMemo, useRef } from 'react';
import { PutManagerSpaceParams } from 'api/managerSpace';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import Toggle from 'components/Toggle/Toggle';
import { Area, Color, ManagerSpace, MapElement } from 'types/common';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';
import { generateSVG } from 'utils/generateSvg';
import { colorSelectOptions, SpaceFormValue, timeUnits } from '../data';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import ColorDot from './ColorDot';
import * as Styled from './Form.styles';
import FormTimeUnitSelect from './FormTimeUnitSelect';
import FormWeekdaySelect from './FormWeekdaySelect';

interface Props {
  mapData: { width: number; height: number; mapElements: MapElement[] };
  spaces: ManagerSpace[];
  selectedSpaceId: number | null;
  disabled: boolean;
  onUpdateSpace: (data: Omit<PutManagerSpaceParams, 'mapId'>) => void;
}

const Form = ({
  mapData,
  spaces,
  selectedSpaceId,
  disabled,
  onUpdateSpace,
}: Props): JSX.Element => {
  const nameInputRef = useRef<HTMLInputElement | null>(null);

  const { values, onChange, onCancel, setValues, getRequestValues } =
    useFormContext(SpaceFormContext);

  const spacesObj = useMemo(() => {
    const result: { [key: string]: ManagerSpace } = {};

    spaces.forEach((space) => (result[space.id] = space));

    return result;
  }, [spaces]);

  const setColor = (color: Color) => {
    setValues({ ...values, color });
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const filteredSpaces = spaces.map((space) =>
      space.id === selectedSpaceId ? { area: values.area as Area, color: values.color } : space
    );
    const mapImageSvg = generateSVG({ ...mapData, spaces: filteredSpaces });
    const valuesForRequest = getRequestValues();

    if (selectedSpaceId !== null) {
      onUpdateSpace({
        spaceId: selectedSpaceId,
        space: {
          mapImageSvg,
          ...valuesForRequest.space,
          settingsRequest: { ...valuesForRequest.space.settingsRequest },
        },
      });
    }
  };

  const handleDelete = () => {
    //
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
      enabledWeekdays: nextEnableWeekdays as SpaceFormValue['enabledWeekdays'],
      area: spacesObj[selectedSpaceId].area,
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedSpaceId, spacesObj]);

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
            <Styled.InputMessage>예약이 열릴 시간과 닫힐 시간을 설정해주세요.</Styled.InputMessage>
          </Styled.Row>

          <Styled.Row>
            <Styled.Fieldset>
              <Styled.Label>예약 시간 단위</Styled.Label>
              <FormTimeUnitSelect
                timeUnits={timeUnits}
                selectedValue={`${values.reservationTimeUnit}`}
                name="reservationTimeUnit"
                onChange={onChange}
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
                step={values.reservationTimeUnit}
                label="최소 예약 시간(분)"
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
                label="최대 예약 시간(분)"
                value={values.reservationMaximumTimeUnit}
                name="reservationMaximumTimeUnit"
                onChange={onChange}
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
              <FormWeekdaySelect onChange={onChange} enabledWeekdays={values.enabledWeekdays} />
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
                <Button type="button" variant="text" onClick={onCancel}>
                  취소
                </Button>
                <Button variant="primary">공간 추가</Button>
              </Styled.FormSubmitContainer>
            )}
          </Styled.Row>
        </Styled.ContentsContainer>
      </Styled.Section>
    </Styled.Form>
  );
};

export default Form;
