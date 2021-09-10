import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import { SpaceEditorMode as Mode } from '../constants';
import * as Styled from './ShapeSelectToolbar.styles';

interface Props {
  mode: Mode;
  setMode: (nextMode: Mode) => void;
}

const ShapeSelectToolbar = ({ mode, setMode }: Props): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.ToolbarButton text="취소" onClick={() => setMode(Mode.Default)}>
        <CloseIcon />
      </Styled.ToolbarButton>
      <Styled.ToolbarButton
        text="사각형"
        selected={mode === Mode.Rect}
        onClick={() => setMode(Mode.Rect)}
      >
        <RectIcon />
      </Styled.ToolbarButton>
    </Styled.Container>
  );
};

export default ShapeSelectToolbar;
