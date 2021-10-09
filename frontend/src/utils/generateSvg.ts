import { EDITOR } from 'constants/editor';
import { WithOptional } from 'types/util';
import { MapElement, ManagerSpace } from './../types/common';

export interface MapSvgData {
  width: number;
  height: number;
  mapElements: WithOptional<MapElement, 'id'>[];
  spaces: WithOptional<ManagerSpace, 'id' | 'name' | 'description' | 'settings'>[];
}

const generateMapSvg = (mapElements: MapSvgData['mapElements']): string =>
  mapElements
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
    .join('');

const generateSpaceSvg = (spaces: MapSvgData['spaces']) =>
  spaces
    .map(({ color, area }) =>
      area.shape === 'rect'
        ? `
        <g>
          <rect
            x='${area.x}'
            y='${area.y}'
            width='${area.width}'
            height='${area.height}'
            fill='${color}'
            opacity='0.3'
          />
        </g>
      `
        : `
        <g>
          <polygon
            points='${area.points.map(({ x, y }) => `${x},${y}`).join(' ')}'
            fill='${color}'
            opacity='0.3'
          />
        </g>
        `
    )
    .join('');

export const generateSvg = ({
  width,
  height,
  mapElements,
  spaces,
}: WithOptional<MapSvgData, 'spaces'>): string =>
  `
    <svg
      xmlns='http://www.w3.org/2000/svg'
      version='1.1'
      width='${width}px'
      height='${height}px'
      viewBox='0 0 ${width} ${height}'
    >
      ${spaces ? generateSpaceSvg(spaces) : ''}
      ${generateMapSvg(mapElements)}
    </svg>
  `
    .replace(/(\r\n\t|\n|\r\t|\s{1,})/gm, ' ')
    .replace(/\s{2,}/g, ' ');
