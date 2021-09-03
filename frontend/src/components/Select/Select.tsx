import { useState, ReactNode } from 'react';
import { ReactComponent as CaretDownIcon } from 'assets/svg/caret-down.svg';
import PALETTE from 'constants/palette';
import * as Styled from './Select.styles';

interface Option {
  value: string;
  title?: string;
  children: ReactNode;
}

export interface Props {
  name: string;
  label: string;
  options: Option[];
  maxheight?: string | number;
  disabled?: boolean;
  value: string;
  onChange: (selectedValue: string) => void;
}

const Select = ({
  name,
  label,
  options,
  maxheight,
  disabled = false,
  value = '',
  onChange = () => null,
}: Props): JSX.Element => {
  const [open, setOpen] = useState(false);

  const selectedOption = options.find((option) => option.value === value);

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  const selectOption = (selectedValue: Props['value']) => {
    setOpen(false);
    onChange(selectedValue);
  };

  return (
    <Styled.Select>
      <Styled.Label id={`${name}-label`}>{label}</Styled.Label>
      <Styled.ListBoxButton
        type="button"
        id={`${name}-button`}
        aria-haspopup="listbox"
        aria-labelledby={`${name}-label ${name}-button`}
        aria-expanded={open}
        disabled={options.length === 0 || disabled}
        onClick={handleToggle}
      >
        <Styled.OptionChildrenWrapper>
          {value === '' && <Styled.ListBoxLabel>{label}</Styled.ListBoxLabel>}
          {selectedOption?.title ? selectedOption?.title : selectedOption?.children}
        </Styled.OptionChildrenWrapper>
        <Styled.CaretDownIconWrapper open={open}>
          <CaretDownIcon fill={PALETTE.GRAY[400]} />
        </Styled.CaretDownIconWrapper>
      </Styled.ListBoxButton>
      {open && (
        <Styled.ListBox
          tabIndex={0}
          role="listbox"
          aria-activedescendant={`${value}`}
          aria-labelledby={`${name}-label`}
          maxheight={maxheight}
        >
          {options.map((option) => (
            <Styled.Option
              key={option.value}
              role="option"
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
