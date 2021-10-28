import React, { useCallback, useEffect, useRef, useState } from 'react';
import { theme } from 'App.styles';
import { ReactComponent as EraserIcon } from 'assets/svg/eraser.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import Board from 'components/Board/Board';
import ColorPicker from 'components/ColorPicker/ColorPicker';
import ColorPickerIcon from 'components/ColorPicker/ColorPickerIcon';
import { EDITOR, KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import useBindKeyPress from 'hooks/board/useBindKeyPress';
import useBoardCoordinate from 'hooks/board/useBoardCoordinate';
import useBoardMove from 'hooks/board/useBoardMove';
import useBoardStatus from 'hooks/board/useBoardStatus';
import useBoardZoom from 'hooks/board/useBoardZoom';
import useMobileRedirect from 'hooks/useMobileRedirect';
import { Color, DrawingStatus, ManagerSpace, MapElement } from 'types/common';
import { MapElementType, MapEditorMode, DrawingAreaShape } from 'types/editor';
import { getPolygonCenterPoint } from 'utils/editor';
import useBoardEraserTool from '../hooks/useBoardEraserTool';
import useBoardLineTool from '../hooks/useBoardLineTool';
import useBoardMoveElement from '../hooks/useBoardMoveElement';
import useBoardRectTool from '../hooks/useBoardRectTool';
import useBoardSelect from '../hooks/useBoardSelect';
import * as Styled from './MapEditor.styles';

const toolbarItems = [
  {
    text: '선택',
    mode: MapEditorMode.Select,
    icon: <SelectIcon />,
  },
  {
    text: '이동',
    mode: MapEditorMode.Move,
    icon: <MoveIcon />,
  },
  {
    text: '선',
    mode: MapEditorMode.Line,
    icon: <LineIcon />,
  },
  {
    text: '사각형',
    mode: MapEditorMode.Rect,
    icon: <RectIcon />,
  },
  {
    text: '지우개',
    mode: MapEditorMode.Eraser,
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

const MapEditor = ({
  spaces,
  mapElementsState: [mapElements, setMapElements],
  boardState: [{ width, height }, onChangeBoard],
}: Props): JSX.Element => {
  useMobileRedirect();

  const boardRef = useRef<SVGSVGElement>(null);

  const [mode, setMode] = useState(MapEditorMode.Select);
  const [color, setColor] = useState<Color>(PALETTE.BLACK[400]);
  const [isColorPickerOpen, setColorPickerOpen] = useState(false);

  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});

  const { pressedKey } = useBindKeyPress();
  const isPressSpacebar = pressedKey === KEY.SPACE;
  const isBoardDraggable = isPressSpacebar || mode === MapEditorMode.Move;
  const isMapElementEventAvailable =
    [MapEditorMode.Select, MapEditorMode.Eraser].includes(mode) && !isBoardDraggable;

  const [boardStatus, setBoardStatus] = useBoardStatus({
    width: Number(width),
    height: Number(height),
  });
  const { stickyDotCoordinate, onMouseMove } = useBoardCoordinate(boardStatus);
  const { onWheel } = useBoardZoom([boardStatus, setBoardStatus]);
  const {
    dragSelectRect,
    selectedMapElements,
    selectedGroupBBox,
    selectRectRef,
    selectedMapElementsGroupRef,
    deselectMapElements,
    setSelectedMapElements,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  } = useBoardSelect({ mapElements, boardRef });
  const { isMoving, onDragStart, onDrag, onDragEnd, onMouseOut } = useBoardMove(
    [boardStatus, setBoardStatus],
    isBoardDraggable
  );
  const { drawLineStart, drawLineEnd } = useBoardLineTool({
    coordinate: stickyDotCoordinate,
    color,
    drawingStatus: [drawingStatus, setDrawingStatus],
    mapElements: [mapElements, setMapElements],
  });
  const { drawRectStart, drawRectEnd } = useBoardRectTool({
    coordinate: stickyDotCoordinate,
    color,
    drawingStatus: [drawingStatus, setDrawingStatus],
    mapElements: [mapElements, setMapElements],
  });
  const { erasingMapElementIds, eraseStart, eraseEnd, onMouseOverMapElement } = useBoardEraserTool({
    mapElements: [mapElements, setMapElements],
  });
  const { isElementMoving, offset, onMoveStartElement, onMoveElement, onMoveEndElement } =
    useBoardMoveElement({
      coordinate: stickyDotCoordinate,
      selectedMapElements,
      setMapElements,
      setSelectedMapElements,
    });

  const toggleColorPicker = () => setColorPickerOpen((prevState) => !prevState);

  const selectMode = (mode: MapEditorMode) => {
    deselectMapElements();
    setDrawingStatus({});
    setMode(mode);
  };

  const handleMouseDown = () => {
    if (isBoardDraggable || isMoving) return;

    if (mode === MapEditorMode.Line) drawLineStart();
    else if (mode === MapEditorMode.Rect) drawRectStart();
    else if (mode === MapEditorMode.Eraser) eraseStart();
  };

  const handleMouseUp = () => {
    if (isBoardDraggable || isMoving) return;

    if (mode === MapEditorMode.Line) drawLineEnd();
    else if (mode === MapEditorMode.Rect) drawRectEnd();
    else if (mode === MapEditorMode.Eraser) eraseEnd();
  };

  const handleDragStartBoard = (event: React.MouseEvent<SVGSVGElement>) => {
    if (isElementMoving) return;

    if (isBoardDraggable) onDragStart(event);
    else if (mode === MapEditorMode.Select) onSelectDragStart(event);
  };

  const handleDragBoard = (event: React.MouseEvent<SVGSVGElement>) => {
    onMouseMove(event);

    if (isBoardDraggable) onDrag(event);
    else if (isElementMoving) onMoveElement();
    else if (mode === MapEditorMode.Select) onSelectDrag(event);
  };

  const handleDragEndBoard = () => {
    if (isBoardDraggable) onDragEnd();
    else if (isElementMoving) onMoveEndElement();
    else if (mode === MapEditorMode.Select) onSelectDragEnd();
  };

  const handleMouseDownSelectedGroup = (event: React.MouseEvent<SVGGElement>) => {
    event.stopPropagation();

    if (!selectedMapElements.length) return;

    onMoveStartElement();
  };

  const deleteMapElement = useCallback(() => {
    if (!selectedMapElements) return;

    setMapElements((prevMapElements) =>
      prevMapElements.filter(({ id }) => !selectedMapElements.find((element) => element.id === id))
    );

    deselectMapElements();
  }, [deselectMapElements, selectedMapElements, setMapElements]);

  useEffect(() => {
    if (!selectedMapElements.length) return;

    const isPressedDeleteKey = pressedKey === KEY.DELETE || pressedKey === KEY.BACK_SPACE;

    if (isPressedDeleteKey && selectedMapElements) {
      deleteMapElement();
    }
  }, [deleteMapElement, pressedKey, selectedMapElements]);

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
          boardState={[boardStatus, setBoardStatus]}
          movable={isBoardDraggable}
          isMoving={isMoving}
          ref={boardRef}
          onMouseDown={handleMouseDown}
          onMouseUp={handleMouseUp}
          onWheel={onWheel}
          onDragStart={handleDragStartBoard}
          onDrag={handleDragBoard}
          onDragEnd={handleDragEndBoard}
          onMouseOut={onMouseOut}
          rootSvgChildren={
            <>
              {dragSelectRect && (
                <rect
                  fill={EDITOR.DRAG_SELECT_RECT_FILL}
                  x={dragSelectRect.x}
                  y={dragSelectRect.y}
                  width={dragSelectRect.width}
                  height={dragSelectRect.height}
                  ref={selectRectRef}
                />
              )}
            </>
          }
        >
          {[MapEditorMode.Line, MapEditorMode.Rect].includes(mode) && (
            <circle
              cx={stickyDotCoordinate.x}
              cy={stickyDotCoordinate.y}
              r={EDITOR.CIRCLE_CURSOR_RADIUS}
              fill={EDITOR.CIRCLE_CURSOR_FILL}
              pointerEvents="none"
            />
          )}

          {spaces.map(({ id, color, area, name }) => (
            <React.Fragment key={id}>
              {area.shape === DrawingAreaShape.Rect && (
                <g pointerEvents="none">
                  <rect
                    x={area.x}
                    y={area.y}
                    width={area.width}
                    height={area.height}
                    fill={color}
                    opacity={EDITOR.SPACE_OPACITY}
                  />
                  <text
                    x={area.x + area.width / 2}
                    y={area.y + area.height / 2}
                    dominantBaseline="middle"
                    textAnchor="middle"
                    fill={EDITOR.TEXT_FILL}
                    fontSize={EDITOR.TEXT_FONT_SIZE}
                    opacity={EDITOR.TEXT_OPACITY}
                  >
                    {name}
                  </text>
                </g>
              )}

              {area.shape === DrawingAreaShape.Polygon && (
                <g pointerEvents="none">
                  <polygon
                    points={area.points.map(({ x, y }) => `${x},${y}`).join(' ')}
                    fill={color}
                    opacity={EDITOR.SPACE_OPACITY}
                  />
                  <text
                    x={getPolygonCenterPoint(area.points).x}
                    y={getPolygonCenterPoint(area.points).y}
                    dominantBaseline="middle"
                    textAnchor="middle"
                    fill={EDITOR.TEXT_FILL}
                    fontSize={EDITOR.TEXT_FONT_SIZE}
                    opacity={EDITOR.TEXT_OPACITY}
                  >
                    {name}
                  </text>
                </g>
              )}
            </React.Fragment>
          ))}

          {drawingStatus.start && mode === MapEditorMode.Line && (
            <polyline
              key={`preview-${MapElementType.Polyline}`}
              points={`${drawingStatus.start.x},${drawingStatus.start.y} ${stickyDotCoordinate.x},${stickyDotCoordinate.y}`}
              stroke={EDITOR.STROKE_PREVIEW}
              strokeWidth={EDITOR.STROKE_WIDTH}
              strokeLinecap="round"
              pointerEvents="none"
            />
          )}

          {drawingStatus.start && mode === MapEditorMode.Rect && (
            <rect
              key={`preview-${MapElementType.Rect}`}
              x={Math.min(drawingStatus.start.x, stickyDotCoordinate.x)}
              y={Math.min(drawingStatus.start.y, stickyDotCoordinate.y)}
              width={Math.abs(drawingStatus.start.x - stickyDotCoordinate.x)}
              height={Math.abs(drawingStatus.start.y - stickyDotCoordinate.y)}
              stroke={EDITOR.STROKE_PREVIEW}
              strokeWidth={EDITOR.STROKE_WIDTH}
              strokeLinecap="round"
              fill="none"
              pointerEvents="none"
            />
          )}

          {mapElements.map((element) => {
            const isSelected = selectedMapElements.find(({ id }) => element.id === id);
            const isDeleting = erasingMapElementIds.includes(element.id);

            if (element.type === MapElementType.Polyline) {
              return (
                <polyline
                  key={`${MapElementType.Polyline}-${element.id}`}
                  id={`${MapElementType.Polyline}-${element.id}`}
                  points={element.points.join(' ')}
                  stroke={element.stroke}
                  strokeWidth={EDITOR.STROKE_WIDTH}
                  strokeLinecap="round"
                  visibility={isSelected ? 'hidden' : 'visible'}
                  pointerEvents={isMapElementEventAvailable ? 'auto' : 'none'}
                  opacity={isDeleting ? EDITOR.OPACITY_DELETING : EDITOR.OPACITY}
                  onMouseOverCapture={onMouseOverMapElement}
                  ref={(el) => (element.ref.current = el)}
                />
              );
            }

            if (element.type === MapElementType.Rect) {
              return (
                <rect
                  key={`${MapElementType.Rect}-${element.id}`}
                  id={`${MapElementType.Rect}-${element.id}`}
                  x={element?.x}
                  y={element?.y}
                  width={element?.width}
                  height={element?.height}
                  stroke={element.stroke}
                  fill="none"
                  strokeWidth={EDITOR.STROKE_WIDTH}
                  strokeLinecap="round"
                  visibility={isSelected ? 'hidden' : 'visible'}
                  pointerEvents={isMapElementEventAvailable ? 'auto' : 'none'}
                  opacity={isDeleting ? EDITOR.OPACITY_DELETING : EDITOR.OPACITY}
                  onMouseOverCapture={onMouseOverMapElement}
                  ref={(el) => (element.ref.current = el)}
                />
              );
            }

            return null;
          })}

          <g
            pointerEvents="none"
            ref={selectedMapElementsGroupRef}
            transform={`matrix(1, 0, 0, 1, ${offset.x}, ${offset.y})`}
          >
            {selectedMapElements.map((element) => {
              if (element.type === MapElementType.Polyline) {
                return (
                  <polyline
                    key={`${MapElementType.Polyline}-${element.id}`}
                    id={`${MapElementType.Polyline}-${element.id}`}
                    points={element.points.join(' ')}
                    stroke={element.stroke}
                    strokeWidth={EDITOR.STROKE_WIDTH}
                    strokeLinecap="round"
                    onMouseOverCapture={onMouseOverMapElement}
                  />
                );
              }

              if (element.type === MapElementType.Rect) {
                return (
                  <rect
                    key={`${MapElementType.Rect}-${element.id}`}
                    id={`${MapElementType.Rect}-${element.id}`}
                    x={element?.x}
                    y={element?.y}
                    width={element?.width}
                    height={element?.height}
                    stroke={element.stroke}
                    fill="none"
                    strokeWidth={EDITOR.STROKE_WIDTH}
                    strokeLinecap="round"
                    onMouseOverCapture={onMouseOverMapElement}
                  />
                );
              }

              return null;
            })}
          </g>

          {selectedGroupBBox && (
            <rect
              x={selectedGroupBBox.x}
              y={selectedGroupBBox.y}
              width={selectedGroupBBox.width}
              height={selectedGroupBBox.height}
              stroke={theme.primary[500]}
              strokeWidth={EDITOR.SELECTED_GROUP_BBOX_STROKE_WIDTH}
              fill="transparent"
              onMouseDown={handleMouseDownSelectedGroup}
            />
          )}
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

export default MapEditor;
