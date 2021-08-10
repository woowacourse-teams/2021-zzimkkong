import { useState, PropsWithChildren, Dispatch, SetStateAction } from 'react';
import { ReactComponent as CaretDownIcon } from 'assets/svg/caret-down.svg';
import PALETTE from 'constants/palette';
import * as Styled from './Select.styles';

interface Option {
  value: string;
}

export interface Props {
  label: string;
  options: PropsWithChildren<Option>[];
  maxheight?: string | number;
  disabled?: boolean;
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
}

const Select = ({
  label,
  options,
  maxheight,
  disabled = false,
  value,
  setValue = () => null,
}: Props): JSX.Element => {
  const [open, setOpen] = useState(false);

  const selectedOption = options.find((option) => option.value === value);

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  const selectOption = (selectedValue: Props['value']) => {
    setValue(selectedValue);
    setOpen(false);
  };

  return (
    <Styled.Select>
      <Styled.Label id="label">{label}</Styled.Label>
      <Styled.ListBoxButton
        type="button"
        id="button"
        aria-haspopup="listbox"
        aria-labelledby="label button"
        aria-expanded={open}
        disabled={options.length === 0 || disabled}
        onClick={handleToggle}
      >
        <Styled.OptionChildrenWrapper>{selectedOption?.children}</Styled.OptionChildrenWrapper>
        <Styled.CaretDownIconWrapper open={open}>
          <CaretDownIcon fill={PALETTE.GRAY[400]} />
        </Styled.CaretDownIconWrapper>
      </Styled.ListBoxButton>
      {open && (
        <Styled.ListBox
          tabIndex={-1}
          role="listbox"
          aria-activedescendant={`${value}`}
          aria-labelledby="label"
          maxheight={maxheight}
        >
          {options.map((option) => (
            <Styled.Option
              key={option.value}
              role="option"
              id={option.value}
              value={option.value}
              aria-selected={option.value === value}
              onClick={() => selectOption(option.value)}
            >
              {option.children}
            </Styled.Option>
          ))}
        </Styled.ListBox>
      )}
    </Styled.Select>
  );
};

export default Select;
