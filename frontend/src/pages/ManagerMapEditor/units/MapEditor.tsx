import React, { useCallback, useEffect, useState } from 'react';
import { ReactComponent as EraserIcon } from 'assets/svg/eraser.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import ColorPicker from 'components/ColorPicker/ColorPicker';
import ColorPickerIcon from 'components/ColorPicker/ColorPickerIcon';
import { EDITOR, KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import useBoardCoordinate from 'pages/ManagerMapEditor/hooks/useBoardCoordinate';
import { Color, DrawingStatus, ManagerSpace, MapElement } from 'types/common';
import { Mode } from 'types/editor';
import useBindKeyPress from '../hooks/useBindKeyPress';
import useBoardEraserTool from '../hooks/useBoardEraserTool';
import useBoardLineTool from '../hooks/useBoardLineTool';
import useBoardMove from '../hooks/useBoardMove';
import useBoardRectTool from '../hooks/useBoardRectTool';
import useBoardSelect from '../hooks/useBoardSelect';
import useBoardStatus from '../hooks/useBoardStatus';
import useBoardZoom from '../hooks/useBoardZoom';
import Board from './Board';
import * as Styled from './MapEditor.styles';

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

interface Props {
  spaces: ManagerSpace[];
  mapElementsState: [MapElement[], React.Dispatch<React.SetStateAction<MapElement[]>>];
  boardState: [
    { width: string; height: string },
    (event: React.ChangeEvent<HTMLInputElement>) => void
  ];
}

const MapCreateEditor = ({
  spaces,
  mapElementsState: [mapElements, setMapElements],
  boardState: [{ width, height }, onChangeBoard],
}: Props): JSX.Element => {
  const [mode, setMode] = useState(Mode.Select);

  const [color, setColor] = useState<Color>(PALETTE.BLACK[400]);
  const [isColorPickerOpen, setColorPickerOpen] = useState(false);

  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});

  const { pressedKey } = useBindKeyPress();
  const isPressSpacebar = pressedKey === KEY.SPACE;
  const isBoardDraggable = isPressSpacebar || mode === Mode.Move;
  const isMapElementClickable = mode === Mode.Select && !isBoardDraggable;
  const isMapElementEventAvailable = [Mode.Select, Mode.Eraser].includes(mode) && !isBoardDraggable;

  const [boardStatus, setBoardStatus] = useBoardStatus({
    width: Number(width),
    height: Number(height),
  });
  const { stickyCoordinate, onMouseMove } = useBoardCoordinate(boardStatus);
  const { onWheel } = useBoardZoom([boardStatus, setBoardStatus]);
  const {
    gripPoints,
    selectedMapElementId,
    selectLineElement,
    selectRectElement,
    deselectMapElement,
  } = useBoardSelect();
  const { isDragging, onDragStart, onDrag, onDragEnd, onMouseOut } = useBoardMove(
    [boardStatus, setBoardStatus],
    isBoardDraggable
  );
  const { drawLineStart, drawLineEnd } = useBoardLineTool({
    coordinate: stickyCoordinate,
    color,
    drawingStatus: [drawingStatus, setDrawingStatus],
    mapElements: [mapElements, setMapElements],
  });
  const { drawRectStart, drawRectEnd } = useBoardRectTool({
    coordinate: stickyCoordinate,
    color,
    drawingStatus: [drawingStatus, setDrawingStatus],
    mapElements: [mapElements, setMapElements],
  });
  const { erasingMapElementIds, eraseStart, eraseEnd, selectErasingElement } = useBoardEraserTool({
    mapElements: [mapElements, setMapElements],
  });

  const toggleColorPicker = () => setColorPickerOpen((prevState) => !prevState);

  const selectMode = (mode: Mode) => {
    setDrawingStatus({});
    setMode(mode);
  };

  const handleMouseDown = () => {
    if (isBoardDraggable || isDragging) return;

    if (mode === Mode.Line) drawLineStart();
    else if (mode === Mode.Rect) drawRectStart();
    else if (mode === Mode.Eraser) eraseStart();
  };

  const handleMouseUp = () => {
    if (isBoardDraggable || isDragging) return;

    if (mode === Mode.Line) drawLineEnd();
    else if (mode === Mode.Rect) drawRectEnd();
    else if (mode === Mode.Eraser) eraseEnd();
  };

  const handleClickBoard = () => {
    if (mode !== Mode.Select) return;

    deselectMapElement();
  };

  const handleClickMapElement = (event: React.MouseEvent<SVGPolylineElement | SVGRectElement>) => {
    if (mode !== Mode.Select) return;

    const target = event.target as SVGElement;
    const [mapElementType, mapElementId] = target.id.split('-');

    if (mapElementType === 'polyline') {
      selectLineElement(event.target as SVGPolylineElement, Number(mapElementId));

      return;
    }

    if (mapElementType === 'rect') {
      selectRectElement(event.target as SVGRectElement, Number(mapElementId));
    }
  };

  const handleMouseOverMapElement = (
    event: React.MouseEvent<SVGPolylineElement | SVGRectElement>
  ) => {
    const target = event.target as SVGElement;
    const [, mapElementId] = target.id.split('-');

    selectErasingElement(Number(mapElementId));
  };

  const deleteMapElement = useCallback(() => {
    if (!selectedMapElementId) return;

    setMapElements((prevMapElements) =>
      prevMapElements.filter(({ id }) => id !== selectedMapElementId)
    );

    deselectMapElement();
  }, [deselectMapElement, selectedMapElementId, setMapElements]);

  useEffect(() => {
    if (mode !== Mode.Select) return;

    const isPressedDeleteKey = pressedKey === KEY.DELETE || pressedKey === KEY.BACK_SPACE;

    if (isPressedDeleteKey && selectedMapElementId) {
      deleteMapElement();
    }
  }, [deleteMapElement, mode, pressedKey, selectedMapElementId]);

  return (
    <Styled.Editor>
      <Styled.Toolbar>
        {toolbarItems.map((item) => (
          <Styled.ToolbarButton
            type="button"
            key={item.text}
            text={item.text}
            selected={mode === item.mode}
            onClick={() => selectMode(item.mode)}
          >
            {item.icon}
          </Styled.ToolbarButton>
        ))}
        <Styled.ToolbarButton type="button" text="색상선택" onClick={toggleColorPicker}>
          <ColorPickerIcon color={color} />
        </Styled.ToolbarButton>
      </Styled.Toolbar>
      <Styled.ColorPicker>
        <ColorPicker open={isColorPickerOpen} color={color} setColor={setColor} />
      </Styled.ColorPicker>
      <Styled.Board>
        <Board
          statusState={[boardStatus, setBoardStatus]}
          isDraggable={isBoardDraggable}
          isDragging={isDragging}
          onClick={handleClickBoard}
          onMouseMove={onMouseMove}
          onMouseDown={handleMouseDown}
          onMouseUp={handleMouseUp}
          onWheel={onWheel}
          onDragStart={onDragStart}
          onDrag={onDrag}
          onDragEnd={onDragEnd}
          onMouseOut={onMouseOut}
        >
          {[Mode.Line, Mode.Rect].includes(mode) && (
            <circle
              cx={stickyCoordinate.x}
              cy={stickyCoordinate.y}
              r={3}
              fill={PALETTE.OPACITY_BLACK[300]}
              pointerEvents="none"
            />
          )}

          {spaces.map(({ id, color, area, name }) => (
            <g key={id} pointerEvents="none">
              <rect
                x={area.x}
                y={area.y}
                width={area.width}
                height={area.height}
                fill={color}
                opacity="0.1"
              />
              <text
                x={area.x + area.width / 2}
                y={area.y + area.height / 2}
                dominantBaseline="middle"
                textAnchor="middle"
                fill={PALETTE.BLACK[700]}
                fontSize="1rem"
                opacity="0.3"
              >
                {name}
              </text>
            </g>
          ))}

          {drawingStatus.start && mode === Mode.Line && (
            <polyline
              key="preview-line"
              points={`${drawingStatus.start.x},${drawingStatus.start.y} ${stickyCoordinate.x},${stickyCoordinate.y}`}
              stroke={PALETTE.OPACITY_BLACK[200]}
              strokeWidth={EDITOR.STROKE_WIDTH}
              strokeLinecap="round"
              pointerEvents="none"
            />
          )}

          {drawingStatus.start && mode === Mode.Rect && (
            <rect
              key="preview-rect"
              x={Math.min(drawingStatus.start.x, stickyCoordinate.x)}
              y={Math.min(drawingStatus.start.y, stickyCoordinate.y)}
              width={Math.abs(drawingStatus.start.x - stickyCoordinate.x)}
              height={Math.abs(drawingStatus.start.y - stickyCoordinate.y)}
              stroke={PALETTE.OPACITY_BLACK[200]}
              strokeWidth={EDITOR.STROKE_WIDTH}
              strokeLinecap="round"
              fill="none"
              pointerEvents="none"
            />
          )}

          {mapElements.map((element) => {
            if (element.type === 'polyline') {
              return (
                <polyline
                  key={`polyline-${element.id}`}
                  id={`polyline-${element.id}`}
                  points={element.points.join(' ')}
                  stroke={element.stroke}
                  strokeWidth={EDITOR.STROKE_WIDTH}
                  strokeLinecap="round"
                  cursor={isMapElementClickable ? 'pointer' : 'default'}
                  pointerEvents={isMapElementEventAvailable ? 'auto' : 'none'}
                  opacity={erasingMapElementIds.includes(element.id) ? '0.3' : '1'}
                  onClickCapture={handleClickMapElement}
                  onMouseOverCapture={handleMouseOverMapElement}
                />
              );
            }

            if (element.type === 'rect') {
              return (
                <rect
                  key={`rect-${element.id}`}
                  id={`rect-${element.id}`}
                  x={element?.x}
                  y={element?.y}
                  width={element?.width}
                  height={element?.height}
                  stroke={element.stroke}
                  fill="none"
                  strokeWidth={EDITOR.STROKE_WIDTH}
                  strokeLinecap="round"
                  cursor={isMapElementClickable ? 'pointer' : 'default'}
                  pointerEvents={isMapElementEventAvailable ? 'auto' : 'none'}
                  opacity={erasingMapElementIds.includes(element.id) ? '0.3' : '1'}
                  onClickCapture={handleClickMapElement}
                  onMouseOverCapture={handleMouseOverMapElement}
                />
              );
            }

            return null;
          })}

          {mode === Mode.Select &&
            gripPoints.map(({ x, y }, index) => (
              <Styled.GripPoint key={index} cx={x} cy={y} r={4} />
            ))}
        </Board>
      </Styled.Board>
      <Styled.Toolbar>
        <Styled.InputWrapper>
          <Styled.Label>
            <Styled.LabelIcon>W</Styled.LabelIcon>
            <Styled.LabelText>넓이</Styled.LabelText>
          </Styled.Label>
          <Styled.SizeInput name="width" value={width} onChange={onChangeBoard} />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <Styled.Label>
            <Styled.LabelIcon>H</Styled.LabelIcon>
            <Styled.LabelText>높이</Styled.LabelText>
          </Styled.Label>
          <Styled.SizeInput name="height" value={height} onChange={onChangeBoard} />
        </Styled.InputWrapper>
      </Styled.Toolbar>
    </Styled.Editor>
  );
};

export default MapCreateEditor;
