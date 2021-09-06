import { useEffect, useState } from 'react';
import { ReactComponent as EraserIcon } from 'assets/svg/eraser.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import ColorPicker from 'components/ColorPicker/ColorPicker';
import ColorPickerIcon from 'components/ColorPicker/ColorPickerIcon';
import PALETTE from 'constants/palette';
import useInput from 'hooks/useInput';
import useInputs from 'hooks/useInputs';
import useBoardCoordinate from 'pages/ManagerMapCreate/hooks/useBoardCoordinate';
import { Color, DrawingStatus, EditorBoard, MapElement } from 'types/common';
import { Mode } from 'types/editor';
import Board from './Board';
import * as Styled from './MapCreateEditor.styles';

const toolbarItems = [
  {
    text: '선택',
    mode: Mode.Select,
    icon: <SelectIcon />,
  },
  {
    text: '이동',
    mode: Mode.Move,
    icon: <MoveIcon />,
  },
  {
    text: '선',
    mode: Mode.Line,
    icon: <LineIcon />,
  },
  {
    text: '사각형',
    mode: Mode.Rect,
    icon: <RectIcon />,
  },
  {
    text: '지우개',
    mode: Mode.Eraser,
    icon: <EraserIcon />,
  },
];

const MapCreateEditor = (): JSX.Element => {
  const [mode, setMode] = useState(Mode.Select);
  const [color, setColor] = useState<Color>(PALETTE.BLACK[400]);
  const [isColorPickerOpen, setColorPickerOpen] = useState(false);

  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});
  const [mapElements, setMapElements] = useState<MapElement[]>([]);

  const [{ width, height }, onChangeBoardSize] = useInputs({
    width: '800',
    height: '600',
  });

  const [boardStatus, setBoardStatus] = useState<EditorBoard>({
    scale: 1,
    x: 0,
    y: 0,
    width: Number(width),
    height: Number(height),
  });

  const { stickyCoordinate, onMouseMove } = useBoardCoordinate(boardStatus);

  const toggleColorPicker = () => setColorPickerOpen((prevState) => !prevState);

  const selectMode = (mode: Mode) => {
    setMode(mode);
  };

  useEffect(() => {
    setBoardStatus((prevStatus) => ({
      ...prevStatus,
      width: Number(width),
      height: Number(height),
    }));
  }, [width, height]);

  return (
    <Styled.Editor>
      <Styled.Toolbar>
        {toolbarItems.map((item) => (
          <Styled.ToolbarButton
            key={item.text}
            text={item.text}
            selected={mode === item.mode}
            onClick={() => selectMode(item.mode)}
          >
            {item.icon}
          </Styled.ToolbarButton>
        ))}
        <Styled.ToolbarButton text="색상선택" onClick={toggleColorPicker}>
          <ColorPickerIcon color={color} />
        </Styled.ToolbarButton>
      </Styled.Toolbar>
      <Styled.ColorPicker>
        <ColorPicker open={isColorPickerOpen} color={color} setColor={setColor} />
      </Styled.ColorPicker>
      <Styled.Board>
        <Board
          statusState={[boardStatus, setBoardStatus]}
          forceDraggable={mode === Mode.Move}
          onMouseMove={onMouseMove}
        ></Board>
      </Styled.Board>
      <Styled.Toolbar>
        <Styled.InputWrapper>
          <Styled.Label>
            <Styled.LabelIcon>W</Styled.LabelIcon>
            <Styled.LabelText>넓이</Styled.LabelText>
          </Styled.Label>
          <Styled.SizeInput name="width" value={width} onChange={onChangeBoardSize} />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <Styled.Label>
            <Styled.LabelIcon>H</Styled.LabelIcon>
            <Styled.LabelText>높이</Styled.LabelText>
          </Styled.Label>
          <Styled.SizeInput name="height" value={height} onChange={onChangeBoardSize} />
        </Styled.InputWrapper>
      </Styled.Toolbar>
    </Styled.Editor>
  );
};

export default MapCreateEditor;
