import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import { ReactComponent as PolygonIcon } from 'assets/svg/polygon.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import useFormContext from 'hooks/useFormContext';
import { SpaceEditorMode as Mode } from 'types/editor';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import * as Styled from './ShapeSelectToolbar.styles';

interface Props {
  mode: Mode;
  setMode: (nextMode: Mode) => void;
}

const ShapeSelectToolbar = ({ mode, setMode }: Props): JSX.Element => {
  const { resetForm } = useFormContext(SpaceFormContext);

  const handleCancel = () => {
    resetForm();
    setMode(Mode.Default);
  };

  return (
    <Styled.Container>
      <Styled.ToolbarButton text="취소" onClick={handleCancel}>
        <CloseIcon />
      </Styled.ToolbarButton>
      <Styled.ToolbarButton
        text="사각형"
        selected={mode === Mode.Rect}
        onClick={() => setMode(Mode.Rect)}
      >
        <RectIcon />
      </Styled.ToolbarButton>
      <Styled.ToolbarButton
        text="다각형"
        selected={mode === Mode.Polygon}
        onClick={() => setMode(Mode.Polygon)}
      >
        <PolygonIcon />
      </Styled.ToolbarButton>
    </Styled.Container>
  );
};

export default ShapeSelectToolbar;
