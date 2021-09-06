import { EDITOR } from 'constants/editor';
import PALETTE from 'constants/palette';

interface BoardSize {
  width: number;
  height: number;
}

const GridPatternDefs = () => (
  <defs>
    <pattern
      id="smallGrid"
      width={`${EDITOR.GRID_SIZE}px`}
      height={`${EDITOR.GRID_SIZE}px`}
      patternUnits="userSpaceOnUse"
    >
      <path
        d={`M ${EDITOR.GRID_SIZE} 0 L 0 0 0 ${EDITOR.GRID_SIZE}`}
        fill="none"
        stroke={PALETTE.GRAY[300]}
        strokeWidth="0.5"
      />
    </pattern>
    <pattern
      id="grid"
      width={`${EDITOR.GRID_SIZE * 10}px`}
      height={`${EDITOR.GRID_SIZE * 10}px`}
      patternUnits="userSpaceOnUse"
    >
      <rect
        width={`${EDITOR.GRID_SIZE * 10}px`}
        height={`${EDITOR.GRID_SIZE * 10}px`}
        fill="url(#smallGrid)"
      />
      <path
        d={`M ${EDITOR.GRID_SIZE * 10} 0 L 0 0 0 ${EDITOR.GRID_SIZE * 10}`}
        fill="none"
        stroke={PALETTE.GRAY[300]}
        strokeWidth="1"
      />
    </pattern>
  </defs>
);

const GridPattern = ({ width, height }: BoardSize): JSX.Element => (
  <rect width={`${width + 0.5}px`} height={`${height + 0.5}px`} fill="url(#grid)" />
);

GridPattern.Defs = GridPatternDefs;

export default GridPattern;
