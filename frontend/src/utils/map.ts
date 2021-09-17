import { EDITOR } from 'constants/editor';
import { ManagerSpace, MapElement } from 'types/common';

interface CreateMapImageSvgParams {
  mapElements: MapElement[];
  spaces: ManagerSpace[];
  width: number | string;
  height: number | string;
}

export const createMapImageSvg = ({
  mapElements,
  spaces,
  width,
  height,
}: CreateMapImageSvgParams): string => {
  const mapImageSvg = `
    <svg
      xmlns='http://www.w3.org/2000/svg'
      version='1.1'
      width='${width}px'
      height='${height}px'
      viewBox='0 0 ${width} ${height}'
    >
      ${spaces
        ?.map(
          ({ color, area }) => `
            <g>
              <rect
                x='${area.x}'
                y='${area.y}'
                width='${area.width}'
                height='${area.height}'
                fill='${color}'
                opacity='0.3'
              />
            </g>`
        )
        .join('')}

      ${mapElements
        .map((element) =>
          element.type === 'polyline'
            ? `
            <polyline
              points='${element.points.join(' ')}'
              stroke='${element.stroke}'
              strokeWidth='${EDITOR.STROKE_WIDTH}'
              strokeLinecap='round'
            />
          `
            : `
            <rect
              x='${element.x ?? 0}'
              y='${element.y ?? 0}'
              width='${element.width ?? 0}'
              height='${element.height ?? 0}'
              stroke='${element.stroke}'
              fill='none'
              strokeWidth='${EDITOR.STROKE_WIDTH}'
            />
            `
        )
        .join('')}
    </svg>
  `
    .replace(/(\r\n\t|\n|\r\t|\s{1,})/gm, ' ')
    .replace(/\s{2,}/g, ' ');

  return mapImageSvg;
};
