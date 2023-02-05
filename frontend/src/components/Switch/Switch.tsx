import React from 'react';
import * as Styled from './Switch.styled';

type Label = string;

interface Props {
  labelList: Label[];
  selectedLabel: Label;
  onClick: (label: Label) => void;
}

const Switch = ({ labelList, selectedLabel, onClick }: Props): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.ButtonContainer>
        {labelList.map((label) => (
          <Styled.SwitchButton
            key={label}
            selected={label === selectedLabel}
            onClick={() => onClick(label)}
          >
            {label}
          </Styled.SwitchButton>
        ))}
      </Styled.ButtonContainer>
    </Styled.Container>
  );
};

export default Switch;
