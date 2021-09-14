import { EDITOR } from 'constants/editor';
import { EditorBoard } from 'types/common';

const useBoardZoom = (
  boardState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>]
): {
  onWheel: (event: React.WheelEvent<SVGSVGElement>) => void;
} => {
  const zoomBoard = ({ offsetX, offsetY, deltaY }: WheelEvent) => {
    const [, setBoard] = boardState;

    setBoard((prevStatus) => {
      const { scale, x, y, width, height } = prevStatus;

      const nextScale = scale - deltaY * EDITOR.SCALE_DELTA;

      if (nextScale <= EDITOR.MIN_SCALE || nextScale >= EDITOR.MAX_SCALE) {
        return {
          ...prevStatus,
          scale: prevStatus.scale,
        };
      }

      const cursorX = (offsetX - x) / (width * scale);
      const cursorY = (offsetY - y) / (height * scale);

      const widthDiff = Math.abs(width * nextScale - width * scale) * cursorX;
      const heightDiff = Math.abs(height * nextScale - height * scale) * cursorY;

      const nextX = nextScale > scale ? x - widthDiff : x + widthDiff;
      const nextY = nextScale > scale ? y - heightDiff : y + heightDiff;

      return {
        ...prevStatus,
        x: nextX,
        y: nextY,
        scale: nextScale,
      };
    });
  };

  const onWheel = (event: React.WheelEvent<SVGElement>) => {
    zoomBoard(event.nativeEvent);
  };

  return {
    onWheel,
  };
};

export default useBoardZoom;
