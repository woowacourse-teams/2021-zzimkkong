import { AxiosError } from 'axios';
import {
  ChangeEventHandler,
  FormEventHandler,
  MouseEventHandler,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
  WheelEventHandler,
} from 'react';
import { useMutation } from 'react-query';
import { Link, Redirect, useParams } from 'react-router-dom';
import { deleteManagerSpace, postManagerSpace, putManagerSpace } from 'api/managerSpace';
import { deletePreset, postPreset } from 'api/presets';
import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import { ReactComponent as PlusSmallIcon } from 'assets/svg/plus-small.svg';
import { ReactComponent as PolygonIcon } from 'assets/svg/polygon.svg';
import { ReactComponent as RectIcon } from 'assets/svg/rect.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import Select from 'components/Select/Select';
import Toggle from 'components/Toggle/Toggle';
import { DrawingAreaShape, EDITOR, KEY } from 'constants/editor';
import MESSAGE from 'constants/message';
import PALETTE from 'constants/palette';
import PATH from 'constants/path';
import SPACE from 'constants/space';
import useInput from 'hooks/useInput';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import useManagerMap from 'hooks/useManagerMap';
import useManagerSpaces from 'hooks/useManagerSpaces';
import usePresets from 'hooks/usePreset';
import useToggle from 'hooks/useToggle';
import {
  Coordinate,
  DrawingStatus,
  ManagerSpace,
  MapDrawing,
  Preset,
  SpaceArea,
} from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';
import * as Styled from './ManagerSpaceEdit.styles';

const colorSelectOptions = [
  PALETTE.RED[500],
  PALETTE.ORANGE[500],
  PALETTE.YELLOW[500],
  PALETTE.GREEN[500],
  PALETTE.BLUE[300],
  PALETTE.BLUE[900],
  PALETTE.PURPLE[500],
];

interface Params {
  mapId: string;
}

interface CreateResponseHeaders {
  location: string;
}

const ManagerSpaceEdit = (): JSX.Element => {
  const editorRef = useRef<HTMLDivElement | null>(null);
  const spaceNameRef = useRef<HTMLInputElement | null>(null);
  const { mapId } = useParams<Params>();

  useListenManagerMainState({ mapId: Number(mapId) });

  const todayDate = formatDate(new Date());
  const initialStartTime = formatTimeWithSecond(new Date(`${todayDate}T07:00:00`));
  const initialEndTime = formatTimeWithSecond(new Date(`${todayDate}T23:00:00`));

  const map = useManagerMap({ mapId: Number(mapId) });
  const mapName = map.data?.data.mapName ?? '';
  const { width, height, mapElements } = useMemo(() => {
    try {
      return JSON.parse(map.data?.data.mapDrawing ?? '{}') as MapDrawing;
    } catch (error) {
      alert(MESSAGE.MANAGER_SPACE.GET_UNEXPECTED_ERROR);

      return { width: 800, height: 600, mapElements: [] };
    }
  }, [map.data?.data.mapDrawing]);

  const managerSpaces = useManagerSpaces({ mapId: Number(mapId) });
  const spaces: ManagerSpace[] = useMemo(
    () =>
      managerSpaces.data?.data.spaces.map((space) => ({
        ...space,
        area: JSON.parse(space.area) as SpaceArea,
      })) ?? [],
    [managerSpaces.data?.data.spaces]
  );
  const spaceOptions = spaces.map(({ id, name, color }) => ({
    value: `${id}`,
    children: (
      <Styled.SpaceOption>
        <Styled.ColorDot size="medium" color={color} />
        {name}
      </Styled.SpaceOption>
    ),
  }));

  const createSpace = useMutation(postManagerSpace, {
    onSuccess: (response) => {
      const { location } = response.headers as CreateResponseHeaders;
      const newSpaceId = Number(location.split('/').pop() ?? '');

      initializeDrawingStatus();
      setAddingSpace(false);
      setSelectedSpaceId(newSpaceId);

      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_CREATED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.ADD_UNEXPECTED_ERROR);
    },
  });

  const updateSpace = useMutation(putManagerSpace, {
    onSuccess: () => {
      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_SETTING_UPDATED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.EDIT_UNEXPECTED_ERROR);
    },
  });

  const deleteSpace = useMutation(deleteManagerSpace, {
    onSuccess: () => {
      setSelectedSpaceId(null);
      setArea(null);

      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_DELETED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.DELETE_UNEXPECTED_ERROR);
    },
  });

  const getPresets = usePresets();
  const presets = useMemo(
    () => getPresets.data?.data?.presets ?? [],
    [getPresets.data?.data?.presets]
  );

  const createPreset = useMutation(postPreset, {
    onSuccess: () => {
      setPresetFormOpen(false);
      getPresets.refetch();
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.ADD_PRESET_UNEXPECTED_ERROR);
    },
  });

  const removePreset = useMutation(deletePreset, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_SPACE.PRESET_DELETED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.DELETE_PRESET_UNEXPECTED_ERROR);
    },
  });

  const [isPresetFormOpen, setPresetFormOpen] = useState(false);
  const [presetName, onChangePresetName] = useInput('');

  const [isDragging, setDragging] = useState(false);
  const [isDraggable, setDraggable] = useState(false);
  const [dragOffsetX, setDragOffsetX] = useState(0);
  const [dragOffsetY, setDragOffsetY] = useState(0);

  const [selectedSpaceId, setSelectedSpaceId] = useState<number | null>(null);
  const [selectedPresetId, setSelectedPresetId] = useState<number | null>(null);

  const [isDrawingArea, setDrawingArea] = useState(false);
  const [isAddingSpace, setAddingSpace] = useState(false);
  const [drawingAreaShape, setDrawingAreaShape] = useState<DrawingAreaShape>(DrawingAreaShape.RECT);
  const [area, setArea] = useState<SpaceArea | null>(null);

  const [spaceName, onChangeSpaceName, setSpaceName] = useInput('');
  const [reservationEnable, onChangeReservationEnable, setReservationEnable] = useToggle(true);
  const [spaceColor, onChangeSpaceColor, setSpaceColor] = useInput(PALETTE.RED[500]);
  const [availableStartTime, onChangeAvailableStartTime, setAvailableStartTime] =
    useInput(initialStartTime);
  const [availableEndTime, onChangeAvailableEndTime, setAvailableEndTime] =
    useInput(initialEndTime);
  const [reservationTimeUnit, onChangeReservationTimeUnit, setReservationTimeUnit] = useInput('10');
  const [
    reservationMinimumTimeUnit,
    onChangeReservationMinimumTimeUnit,
    setReservationMinimumTimeUnit,
  ] = useInput('10');
  const [
    reservationMaximumTimeUnit,
    onChangeReservationMaximumTimeUnit,
    setReservationMaximumTimeUnit,
  ] = useInput('1440');
  const [enabledWeekdays, setEnabledWeekdays] = useState({
    monday: true,
    tuesday: true,
    wednesday: true,
    thursday: true,
    friday: true,
    saturday: true,
    sunday: true,
  });
  const { monday, tuesday, wednesday, thursday, friday, saturday, sunday } = enabledWeekdays;

  const enabledDayOfWeek = Object.entries(enabledWeekdays)
    .filter(([, checked]) => checked)
    .map(([weekday]) => weekday)
    .join(',');

  const [board, setBoard] = useState({
    width: 0,
    height: 0,
    x: 0,
    y: 0,
    scale: 1,
  });

  const [coordinate, setCoordinate] = useState<Coordinate>({
    x: -EDITOR.GRID_SIZE,
    y: -EDITOR.GRID_SIZE,
  });
  const stickyCoordinate: Coordinate = useMemo(
    () => ({
      x: Math.floor(coordinate.x / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
      y: Math.floor(coordinate.y / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
    }),
    [coordinate]
  );

  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});
  const [guideArea, setGuideArea] = useState<SpaceArea>({
    shape: DrawingAreaShape.RECT,
    x: 0,
    y: 0,
    width: 0,
    height: 0,
  });

  const handleWheel: WheelEventHandler<SVGElement> = useCallback((event) => {
    const { offsetX, offsetY, deltaY } = event.nativeEvent;

    setBoard((prevState) => {
      const { scale, x, y, width, height } = prevState;

      const nextScale = scale - deltaY * EDITOR.SCALE_DELTA;

      if (nextScale <= EDITOR.MIN_SCALE || nextScale >= EDITOR.MAX_SCALE) {
        return {
          ...prevState,
          scale: prevState.scale,
        };
      }

      const cursorX = (offsetX - x) / (width * scale);
      const cursorY = (offsetY - y) / (height * scale);

      const widthDiff = Math.abs(width * nextScale - width * scale) * cursorX;
      const heightDiff = Math.abs(height * nextScale - height * scale) * cursorY;

      const nextX = nextScale > scale ? x - widthDiff : x + widthDiff;
      const nextY = nextScale > scale ? y - heightDiff : y + heightDiff;

      return {
        ...prevState,
        x: nextX,
        y: nextY,
        scale: nextScale,
      };
    });
  }, []);

  const handleDragStart: MouseEventHandler<SVGElement> = useCallback(
    (event) => {
      if (isDrawingArea || !isDraggable) return;

      setDragOffsetX(event.nativeEvent.offsetX - board.x);
      setDragOffsetY(event.nativeEvent.offsetY - board.y);

      setDragging(true);
    },
    [board.x, board.y, isDraggable, isDrawingArea]
  );

  const handleDragEnd = useCallback(() => {
    setDragOffsetX(0);
    setDragOffsetY(0);

    setDragging(false);
  }, []);

  const handleDrag: MouseEventHandler<SVGElement> = useCallback(
    (event) => {
      if (isDrawingArea || !isDragging || !isDraggable) return;

      const { offsetX, offsetY } = event.nativeEvent;

      setBoard((prevState) => ({
        ...prevState,
        x: offsetX - dragOffsetX,
        y: offsetY - dragOffsetY,
      }));
    },
    [dragOffsetX, dragOffsetY, isDraggable, isDragging, isDrawingArea]
  );

  const handleMouseOut = useCallback(() => {
    setDraggable(false);
    setDragging(false);
  }, []);

  const getSVGCoordinate = useCallback(
    (event: React.MouseEvent<SVGElement>) => {
      const svg = (event.nativeEvent.target as SVGElement)?.ownerSVGElement;
      if (!svg) return { svg: null, x: -1, y: -1 };

      let point = svg.createSVGPoint();

      point.x = event.nativeEvent.clientX;
      point.y = event.nativeEvent.clientY;
      point = point.matrixTransform(svg.getScreenCTM()?.inverse());

      const x = (point.x - board.x) * (1 / board.scale);
      const y = (point.y - board.y) * (1 / board.scale);

      return { svg, x, y };
    },
    [board.scale, board.x, board.y]
  );

  const initializeDrawingStatus = useCallback(() => {
    setDrawingStatus({});
    setGuideArea({ shape: DrawingAreaShape.RECT, x: 0, y: 0, width: 0, height: 0 });
  }, []);

  const initializeSpaceAddForm = useCallback(() => {
    setSpaceName('');
    setSpaceColor(PALETTE.RED[500]);
    setAvailableStartTime(initialStartTime);
    setAvailableEndTime(initialEndTime);
    setReservationTimeUnit('10');
    setReservationMinimumTimeUnit('10');
    setReservationMaximumTimeUnit('1440');
    setReservationEnable(true);
    setEnabledWeekdays({
      monday: true,
      tuesday: true,
      wednesday: true,
      thursday: true,
      friday: true,
      saturday: true,
      sunday: true,
    });
  }, [
    initialEndTime,
    initialStartTime,
    setAvailableEndTime,
    setAvailableStartTime,
    setReservationEnable,
    setReservationMaximumTimeUnit,
    setReservationMinimumTimeUnit,
    setReservationTimeUnit,
    setSpaceName,
    setSpaceColor,
  ]);

  const handleChangeReservationForm = (
    event: React.ChangeEvent<HTMLInputElement>,
    callback: ChangeEventHandler<HTMLInputElement>
  ) => {
    if (selectedPresetId) selectPreset(null);

    callback(event);
  };

  const handleSubmitPreset: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const availableTime = {
      start: formatTimeWithSecond(new Date(`${todayDate}T${availableStartTime}`)),
      end: formatTimeWithSecond(new Date(`${todayDate}T${availableEndTime}`)),
    };

    const settingsRequest = {
      availableStartTime: availableTime.start,
      availableEndTime: availableTime.end,
      reservationTimeUnit: Number(reservationTimeUnit),
      reservationMinimumTimeUnit: Number(reservationMinimumTimeUnit),
      reservationMaximumTimeUnit: Number(reservationMaximumTimeUnit),
      reservationEnable,
      enabledDayOfWeek,
    };

    createPreset.mutate({ name: presetName, settingsRequest });
  };

  const handleAddPreset = () => {
    setPresetFormOpen(true);
  };

  const handleDeletePreset = (event: React.MouseEvent<HTMLButtonElement>, id: Preset['id']) => {
    event.stopPropagation();
    if (!window.confirm('이 프리셋을 삭제하시겠어요?')) return;

    removePreset.mutate({ id });
  };

  const selectPreset = useCallback(
    (id: number | null) => {
      setSelectedPresetId(id);

      if (id === null) return;

      const {
        availableStartTime,
        availableEndTime,
        reservationTimeUnit,
        reservationMinimumTimeUnit,
        reservationMaximumTimeUnit,
        enabledDayOfWeek,
      } = presets.find((preset) => preset.id === id) ?? presets[0];

      const enableWeekdays = enabledDayOfWeek?.toLowerCase()?.split(',') ?? [];

      setAvailableStartTime(availableStartTime);
      setAvailableEndTime(availableEndTime);
      setReservationTimeUnit(`${reservationTimeUnit}`);
      setReservationMinimumTimeUnit(`${reservationMinimumTimeUnit}`);
      setReservationMaximumTimeUnit(`${reservationMaximumTimeUnit}`);
      setEnabledWeekdays({
        monday: enableWeekdays.includes('monday'),
        tuesday: enableWeekdays.includes('tuesday'),
        wednesday: enableWeekdays.includes('wednesday'),
        thursday: enableWeekdays.includes('thursday'),
        friday: enableWeekdays.includes('friday'),
        saturday: enableWeekdays.includes('saturday'),
        sunday: enableWeekdays.includes('sunday'),
      });
    },
    [
      presets,
      setAvailableEndTime,
      setAvailableStartTime,
      setReservationMaximumTimeUnit,
      setReservationMinimumTimeUnit,
      setReservationTimeUnit,
    ]
  );

  const selectSpace = useCallback(
    (id: number | null) => {
      selectPreset(null);
      setSelectedSpaceId(id);

      if (id === null) return;

      const selectedSpace = spaces.find((space) => space.id === id);
      if (!selectedSpace?.id) return;

      const { name, color, area, settings } = selectedSpace;
      const {
        availableStartTime,
        availableEndTime,
        reservationTimeUnit,
        reservationMinimumTimeUnit,
        reservationMaximumTimeUnit,
        reservationEnable,
        enabledDayOfWeek,
      } = settings;

      const enableWeekdays = enabledDayOfWeek?.toLowerCase()?.split(',') ?? [];

      setArea(area);
      setSpaceName(name);
      setSpaceColor(color ?? PALETTE.RED[500]);
      setAvailableStartTime(availableStartTime);
      setAvailableEndTime(availableEndTime);
      setReservationTimeUnit(`${reservationTimeUnit}`);
      setReservationMinimumTimeUnit(`${reservationMinimumTimeUnit}`);
      setReservationMaximumTimeUnit(`${reservationMaximumTimeUnit}`);
      setReservationEnable(reservationEnable);
      setEnabledWeekdays({
        monday: enableWeekdays.includes('monday'),
        tuesday: enableWeekdays.includes('tuesday'),
        wednesday: enableWeekdays.includes('wednesday'),
        thursday: enableWeekdays.includes('thursday'),
        friday: enableWeekdays.includes('friday'),
        saturday: enableWeekdays.includes('saturday'),
        sunday: enableWeekdays.includes('sunday'),
      });
    },
    [
      selectPreset,
      setAvailableEndTime,
      setAvailableStartTime,
      setReservationEnable,
      setReservationMaximumTimeUnit,
      setReservationMinimumTimeUnit,
      setReservationTimeUnit,
      setSpaceColor,
      setSpaceName,
      spaces,
    ]
  );

  const handleMouseMove: MouseEventHandler<SVGElement> = useCallback(
    (event) => {
      const { x, y } = getSVGCoordinate(event);
      setCoordinate({ x, y });

      if (!drawingStatus.start) return;

      const [startX, endX] =
        drawingStatus.start.x > stickyCoordinate.x
          ? [stickyCoordinate.x, drawingStatus.start.x]
          : [drawingStatus.start.x, stickyCoordinate.x];
      const [startY, endY] =
        drawingStatus.start.y > stickyCoordinate.y
          ? [stickyCoordinate.y, drawingStatus.start.y]
          : [drawingStatus.start.y, stickyCoordinate.y];

      const width = Math.abs(startX - endX) + EDITOR.GRID_SIZE;
      const height = Math.abs(startY - endY) + EDITOR.GRID_SIZE;

      setGuideArea({
        shape: drawingAreaShape,
        x: startX,
        y: startY,
        width,
        height,
      });
    },
    [
      drawingAreaShape,
      drawingStatus.start,
      getSVGCoordinate,
      stickyCoordinate.x,
      stickyCoordinate.y,
    ]
  );

  const handleDrawStart: MouseEventHandler<SVGElement> = useCallback(() => {
    if (!isDrawingArea || !drawingAreaShape) return;

    setDrawingStatus((prevDrawingStatus) => ({
      ...prevDrawingStatus,
      start: stickyCoordinate,
    }));
  }, [drawingAreaShape, isDrawingArea, stickyCoordinate]);

  const handleDrawEnd: MouseEventHandler<SVGElement> = useCallback(() => {
    if (!isDrawingArea || !drawingAreaShape) return;
    if (!drawingStatus || !drawingStatus.start) return;

    const [startX, endX] =
      drawingStatus.start.x > stickyCoordinate.x
        ? [stickyCoordinate.x, drawingStatus.start.x]
        : [drawingStatus.start.x, stickyCoordinate.x];
    const [startY, endY] =
      drawingStatus.start.y > stickyCoordinate.y
        ? [stickyCoordinate.y, drawingStatus.start.y]
        : [drawingStatus.start.y, stickyCoordinate.y];

    const width = Math.abs(startX - endX) + EDITOR.GRID_SIZE;
    const height = Math.abs(startY - endY) + EDITOR.GRID_SIZE;

    setArea({
      shape: DrawingAreaShape.RECT,
      x: startX,
      y: startY,
      width,
      height,
    });

    initializeSpaceAddForm();
    initializeDrawingStatus();
    setDrawingArea(false);

    spaceNameRef.current?.focus();
  }, [
    drawingAreaShape,
    drawingStatus,
    initializeDrawingStatus,
    initializeSpaceAddForm,
    isDrawingArea,
    stickyCoordinate.x,
    stickyCoordinate.y,
  ]);

  const handleChangeEnabledDayOfWeek: ChangeEventHandler<HTMLInputElement> = useCallback(
    (event) => {
      const { name, checked } = event.target;

      setEnabledWeekdays((prevDayOfWeek) => ({
        ...prevDayOfWeek,
        [name]: checked,
      }));
    },
    []
  );

  const handleCancelForm = useCallback(() => {
    if (!window.confirm(MESSAGE.MANAGER_SPACE.CANCEL_ADD_SPACE_CONFIRM)) return;
    if (!selectedSpaceId) setArea(null);

    initializeDrawingStatus();
    setAddingSpace(false);
    selectSpace(selectedSpaceId);
  }, [initializeDrawingStatus, selectSpace, selectedSpaceId]);

  const handleCancelAddingSpace = useCallback(() => {
    if (selectedSpaceId) selectSpace(selectedSpaceId);

    initializeDrawingStatus();
    setAddingSpace(false);
    setDrawingArea(false);
  }, [initializeDrawingStatus, selectSpace, selectedSpaceId]);

  const handleDeleteSpace = useCallback(() => {
    if (!selectedSpaceId) return;
    if (!window.confirm(MESSAGE.MANAGER_SPACE.DELETE_SPACE_CONFIRM)) return;

    const mapImageSvg = `
  <svg
    xmlns='http://www.w3.org/2000/svg'
    version='1.1'
    width='${width}px'
    height='${height}px'
    viewBox='0 0 ${width} ${height}'
  >
    ${spaces
      .filter(({ id }) => id !== selectedSpaceId)
      .map(
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
          </g>
        `
      )
      .join('')}
    ${mapElements
      ?.map(
        ({ points, stroke }) => `
          <polyline
            points='${points.join(' ')}'
            stroke='${stroke}'
            strokeWidth='2'
          />
        `
      )
      .join('')}
  </svg>
`
      .replace(/(\r\n\t|\n|\r\t|\s{1,})/gm, ' ')
      .replace(/\s{2,}/g, ' ');

    deleteSpace.mutate({
      mapId: Number(mapId),
      spaceId: selectedSpaceId,
      mapImageSvg,
    });
  }, [deleteSpace, height, mapElements, mapId, selectedSpaceId, spaces, width]);

  const handleAddSpace = useCallback(() => {
    setArea(null);
    initializeDrawingStatus();
    setAddingSpace(true);
    setDrawingArea(true);
  }, [initializeDrawingStatus]);

  const handleClickSpaceArea = useCallback(
    (id) => {
      if (isAddingSpace) return;

      selectSpace(id);
    },
    [selectSpace, isAddingSpace]
  );

  const handleSubmitSpace: FormEventHandler<HTMLFormElement> = useCallback(
    (event) => {
      event.preventDefault();

      const availableTime = {
        start: formatTimeWithSecond(new Date(`${todayDate}T${availableStartTime}`)),
        end: formatTimeWithSecond(new Date(`${todayDate}T${availableEndTime}`)),
      };

      const targetSpaces =
        selectedSpaceId && !isAddingSpace
          ? spaces.filter(({ id }) => selectedSpaceId !== id)
          : spaces;

      const mapImageSvg = `
    <svg
      xmlns='http://www.w3.org/2000/svg'
      version='1.1'
      width='${width}px'
      height='${height}px'
      viewBox='0 0 ${width} ${height}'
    >
    ${
      area
        ? `
        <g>
          <rect
            x='${area.x}'
            y='${area.y}'
            width='${area.width}'
            height='${area.height}'
            fill='${spaceColor}'
            opacity='0.3'
          />
        </g>
      `
        : ''
    }

      ${targetSpaces
        .map(
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
            </g>
          `
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
                fill="none"
                strokeWidth='${EDITOR.STROKE_WIDTH}'
              />
              `
          )
          .join('')}
    </svg>
  `
        .replace(/(\r\n\t|\n|\r\t|\s{1,})/gm, ' ')
        .replace(/\s{2,}/g, ' ');

      if (isAddingSpace) {
        createSpace.mutate({
          mapId: Number(mapId),
          space: {
            name: spaceName,
            color: spaceColor,
            description: spaceName,
            area: JSON.stringify(area),
            settingsRequest: {
              availableStartTime: availableTime.start,
              availableEndTime: availableTime.end,
              reservationTimeUnit: Number(reservationTimeUnit),
              reservationMinimumTimeUnit: Number(reservationMinimumTimeUnit),
              reservationMaximumTimeUnit: Number(reservationMaximumTimeUnit),
              reservationEnable,
              enabledDayOfWeek,
            },
            mapImageSvg,
          },
        });

        return;
      }

      if (selectedSpaceId && !isAddingSpace) {
        updateSpace.mutate({
          mapId: Number(mapId),
          spaceId: selectedSpaceId,
          space: {
            name: spaceName,
            color: spaceColor,
            description: spaceName,
            area: JSON.stringify(area),
            settingsRequest: {
              availableStartTime: availableTime.start,
              availableEndTime: availableTime.end,
              reservationTimeUnit: Number(reservationTimeUnit),
              reservationMinimumTimeUnit: Number(reservationMinimumTimeUnit),
              reservationMaximumTimeUnit: Number(reservationMaximumTimeUnit),
              reservationEnable,
              enabledDayOfWeek,
            },
            mapImageSvg,
          },
        });
      }
    },
    [
      area,
      availableEndTime,
      availableStartTime,
      createSpace,
      enabledDayOfWeek,
      height,
      isAddingSpace,
      mapElements,
      mapId,
      reservationEnable,
      reservationMaximumTimeUnit,
      reservationMinimumTimeUnit,
      reservationTimeUnit,
      selectedSpaceId,
      spaceColor,
      spaceName,
      spaces,
      todayDate,
      updateSpace,
      width,
    ]
  );

  const handleKeyDown = useCallback((event: KeyboardEvent) => {
    if ((event.target as HTMLElement).tagName === 'INPUT') return;

    if (event.key === KEY.SPACE) setDraggable(true);
  }, []);

  const handleKeyUp = useCallback((event: KeyboardEvent) => {
    if (event.key === KEY.SPACE) setDraggable(false);
  }, []);

  const presetOptions = presets.map(({ id, name }) => ({
    value: `${id}`,
    title: name,
    children: (
      <Styled.PresetOption>
        <Styled.PresetName>{name}</Styled.PresetName>
        <IconButton type="button" onClickCapture={(event) => handleDeletePreset(event, id)}>
          <DeleteIcon />
        </IconButton>
      </Styled.PresetOption>
    ),
  }));

  useEffect(() => {
    const editorWidth = editorRef.current ? editorRef.current.offsetWidth : 0;
    const editorHeight = editorRef.current ? editorRef.current.offsetHeight : 0;

    setBoard((prevState) => ({
      ...prevState,
      x: (editorWidth - board.width) / 2,
      y: (editorHeight - board.height) / 2,
    }));
  }, [board.width, board.height]);

  useEffect(() => {
    if (width && height) {
      setBoard((prevBoard) => ({
        ...prevBoard,
        width,
        height,
      }));
    }
  }, [width, height]);

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('keyup', handleKeyUp);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.removeEventListener('keyup', handleKeyUp);
    };
  }, [handleKeyDown, handleKeyUp]);

  if (
    (map.isError && map.error?.response?.status === 401) ||
    (managerSpaces.isError && managerSpaces.error?.response?.status === 401)
  ) {
    return <Redirect to={PATH.MANAGER_LOGIN} />;
  }

  if (map.isError || managerSpaces.isError) {
    return <Redirect to={PATH.MANAGER_MAIN} />;
  }

  return (
    <>
      <Header />
      <Layout>
        <Styled.Page>
          <Styled.EditorHeader>
            <Styled.MapName>{mapName}</Styled.MapName>
            <Styled.ButtonContainer>
              <Link to={PATH.MANAGER_MAIN}>
                <Button variant="primary">공간 편집 끝내기</Button>
              </Link>
            </Styled.ButtonContainer>
          </Styled.EditorHeader>
          <Styled.EditorContainer>
            <Styled.EditorContent>
              {isDrawingArea && (
                <Styled.SpaceShapeSelect>
                  <Styled.ToolbarButton text="취소" onClick={handleCancelAddingSpace}>
                    <CloseIcon />
                  </Styled.ToolbarButton>
                  <Styled.ToolbarButton
                    text="사각형"
                    selected={drawingAreaShape === DrawingAreaShape.RECT}
                    onClick={() => setDrawingAreaShape(DrawingAreaShape.RECT)}
                  >
                    <RectIcon />
                  </Styled.ToolbarButton>

                  {/* NOTE 추후 다각형 기능 구현 시, 이 부분의 주석을 해제하고 작성하면 됩니다. */}
                  {/* <Styled.ToolbarButton
                    text="다각형"
                    selected={drawingAreaShape === DrawingAreaShape.POLYGON}
                    onClick={() => setDrawingAreaShape(DrawingAreaShape.POLYGON)}
                  >
                    <PolygonIcon />
                  </Styled.ToolbarButton> */}
                </Styled.SpaceShapeSelect>
              )}
              <Styled.Editor ref={editorRef}>
                <Styled.BoardContainer
                  xmlns="http://www.w3.org/2000/svg"
                  version="1.1"
                  width="100%"
                  height="100%"
                  isDragging={isDragging}
                  isDraggable={isDraggable}
                  onWheel={handleWheel}
                  onMouseDown={handleDragStart}
                  onMouseUp={handleDragEnd}
                  onMouseOut={handleMouseOut}
                  onMouseMove={handleDrag}
                >
                  <rect width="100%" height="100%" fill={PALETTE.GRAY[200]}></rect>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    version="1.1"
                    onMouseMove={handleMouseMove}
                    onMouseDown={handleDrawStart}
                    onMouseUp={handleDrawEnd}
                  >
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
                    <g
                      id="board"
                      transform={`matrix(${board.scale}, 0, 0, ${board.scale}, ${board.x}, ${board.y})`}
                    >
                      <rect width={`${board.width}px`} height={`${board.height}px`} fill="white" />

                      {/* Note: 새로 그려지는 중인 공간의 영역 */}
                      {guideArea && (
                        <rect
                          x={guideArea.x}
                          y={guideArea.y}
                          width={guideArea.width}
                          height={guideArea.height}
                          fill={PALETTE.OPACITY_BLACK[100]}
                        />
                      )}

                      {/* Note: 커서 위치 표시 */}
                      {isDrawingArea && (
                        <rect
                          x={stickyCoordinate.x}
                          y={stickyCoordinate.y}
                          width={EDITOR.GRID_SIZE}
                          height={EDITOR.GRID_SIZE}
                          fill={PALETTE.OPACITY_BLACK[100]}
                        />
                      )}

                      {/* Note: 모눈 표시 */}
                      <rect
                        width={`${board.width + 0.5}px`}
                        height={`${board.height + 0.5}px`}
                        fill="url(#grid)"
                        pointerEvents="none"
                      />

                      {/* Note: 현재 추가 혹은 삭제 중인 공간의 영역 */}
                      {area && (
                        <g>
                          {area.shape === DrawingAreaShape.RECT && (
                            <rect
                              x={area.x}
                              y={area.y}
                              width={area.width}
                              height={area.height}
                              fill={spaceColor ?? PALETTE.WHITE}
                              opacity="0.3"
                            />
                          )}

                          <Styled.SpaceAreaText
                            x={area.x + area.width / 2}
                            y={area.y + area.height / 2}
                          >
                            {spaceName}
                          </Styled.SpaceAreaText>
                        </g>
                      )}

                      {/* Note: 공간 영역 */}
                      {spaces.map(({ id, color, area, name }) => (
                        <g key={id}>
                          <Styled.SpaceAreaRect
                            x={area.x}
                            y={area.y}
                            width={area.width}
                            height={area.height}
                            fill={
                              !isAddingSpace && id === selectedSpaceId
                                ? spaceColor
                                : color ?? PALETTE.GRAY[200]
                            }
                            onClick={() => handleClickSpaceArea(id)}
                            disabled={isAddingSpace || id === selectedSpaceId}
                          />
                          {(isAddingSpace || id !== selectedSpaceId) && (
                            <Styled.SpaceAreaText
                              x={area.x + area.width / 2}
                              y={area.y + area.height / 2}
                            >
                              {name}
                            </Styled.SpaceAreaText>
                          )}
                        </g>
                      ))}

                      {/* Note: 맵 요소 */}
                      {mapElements?.map((element) =>
                        element.type === 'polyline' ? (
                          <polyline
                            key={`${element.id}`}
                            points={element.points.join(' ')}
                            stroke={element.stroke}
                            strokeWidth={EDITOR.STROKE_WIDTH}
                            strokeLinecap="round"
                            pointerEvents="none"
                          />
                        ) : (
                          <rect
                            key={`square-${element.id}`}
                            x={element?.x}
                            y={element?.y}
                            width={element?.width}
                            height={element?.height}
                            stroke={element.stroke}
                            fill="none"
                            strokeWidth={EDITOR.STROKE_WIDTH}
                            strokeLinecap="round"
                          />
                        )
                      )}
                    </g>
                  </svg>
                </Styled.BoardContainer>
              </Styled.Editor>
            </Styled.EditorContent>
            <Styled.FormContainer disabled={isDrawingArea}>
              <Styled.SpaceSelect>
                <Styled.FormHeader>공간 선택</Styled.FormHeader>
                <Styled.SpaceSelectWrapper>
                  <Select
                    name="space"
                    label="공간 선택"
                    options={spaceOptions}
                    disabled={isAddingSpace}
                    value={`${selectedSpaceId ?? ''}`}
                    onChange={(id) => selectSpace(Number(id))}
                  />
                </Styled.SpaceSelectWrapper>
                <Styled.AddButtonWrapper>
                  <Button variant="primary" shape="round" onClick={handleAddSpace}>
                    <PlusSmallIcon /> 공간 추가
                  </Button>
                </Styled.AddButtonWrapper>
              </Styled.SpaceSelect>
              {selectedSpaceId || area || isAddingSpace ? (
                <Styled.Form onSubmit={handleSubmitSpace} disabled={isDrawingArea}>
                  <Styled.SpaceSettingHeader>
                    <Styled.FormHeader>공간 설정</Styled.FormHeader>
                    <Toggle
                      variant="primary"
                      text="예약 비활성화"
                      checkedText="예약 활성화"
                      textPosition="left"
                      checked={reservationEnable}
                      onChange={onChangeReservationEnable}
                    />
                  </Styled.SpaceSettingHeader>
                  <Styled.FormRow>
                    <Input
                      icon={<Styled.ColorDot color={spaceColor} size="medium" />}
                      label="공간 이름"
                      value={spaceName}
                      onChange={onChangeSpaceName}
                      ref={spaceNameRef}
                      required
                    />
                  </Styled.FormRow>
                  <Styled.FormRow>
                    <Styled.ColorSelect>
                      <Styled.ColorInputLabel>
                        <PaletteIcon />
                        <Styled.ColorInput
                          type="color"
                          value={spaceColor}
                          onChange={onChangeSpaceColor}
                          required
                        />
                      </Styled.ColorInputLabel>
                      {colorSelectOptions.map((color) => (
                        <Styled.ColorDotButton
                          key={color}
                          type="button"
                          onClick={() => setSpaceColor(color)}
                        >
                          <Styled.ColorDot size="large" color={color} />
                        </Styled.ColorDotButton>
                      ))}
                    </Styled.ColorSelect>
                  </Styled.FormRow>
                  <Styled.FormHeader>예약 조건</Styled.FormHeader>
                  {/* Note: 프리셋 기능을 구현할 때 이 UI를 활성화하면 됩니다 */}
                  <Styled.FormRow>
                    <Styled.PresetSelect>
                      <Styled.PresetSelectWrapper>
                        <Select
                          name="preset"
                          label="프리셋 선택"
                          options={presetOptions}
                          value={`${selectedPresetId ?? ''}`}
                          onChange={(id) => selectPreset(Number(id))}
                        />
                      </Styled.PresetSelectWrapper>
                      <Button type="button" onClick={handleAddPreset}>
                        추가
                      </Button>
                    </Styled.PresetSelect>
                  </Styled.FormRow>
                  <Styled.FormRow>
                    <Styled.InputWrapper>
                      <Input
                        type="time"
                        label="예약이 열릴 시간"
                        value={availableStartTime}
                        onChange={(event) =>
                          handleChangeReservationForm(event, onChangeAvailableStartTime)
                        }
                        required
                      />
                      <Input
                        type="time"
                        label="예약이 닫힐 시간"
                        value={availableEndTime}
                        onChange={(event) =>
                          handleChangeReservationForm(event, onChangeAvailableEndTime)
                        }
                        required
                      />
                    </Styled.InputWrapper>
                    <Styled.InputMessage>
                      예약이 열리는 시간과 닫히는 시간을 설정할 수 있습니다.
                    </Styled.InputMessage>
                  </Styled.FormRow>

                  <Styled.FormRow>
                    <Styled.Fieldset>
                      <Styled.FormLabel>예약 시간 단위</Styled.FormLabel>
                      <Styled.InputWrapper>
                        <Styled.RadioLabel>
                          <Styled.RadioInput
                            type="radio"
                            checked={reservationTimeUnit === '5'}
                            value="5"
                            onChange={(event) =>
                              handleChangeReservationForm(event, onChangeReservationTimeUnit)
                            }
                            name="time-unit"
                            required
                          />
                          <Styled.RadioLabelText>5분</Styled.RadioLabelText>
                        </Styled.RadioLabel>
                        <Styled.RadioLabel>
                          <Styled.RadioInput
                            type="radio"
                            checked={reservationTimeUnit === '10'}
                            value="10"
                            onChange={(event) =>
                              handleChangeReservationForm(event, onChangeReservationTimeUnit)
                            }
                            name="time-unit"
                          />
                          <Styled.RadioLabelText>10분</Styled.RadioLabelText>
                        </Styled.RadioLabel>
                        <Styled.RadioLabel>
                          <Styled.RadioInput
                            type="radio"
                            checked={reservationTimeUnit === '30'}
                            value="30"
                            onChange={(event) =>
                              handleChangeReservationForm(event, onChangeReservationTimeUnit)
                            }
                            name="time-unit"
                          />
                          <Styled.RadioLabelText>30분</Styled.RadioLabelText>
                        </Styled.RadioLabel>
                        <Styled.RadioLabel>
                          <Styled.RadioInput
                            type="radio"
                            checked={reservationTimeUnit === '60'}
                            value="60"
                            onChange={(event) =>
                              handleChangeReservationForm(event, onChangeReservationTimeUnit)
                            }
                            name="time-unit"
                          />
                          <Styled.RadioLabelText>1시간</Styled.RadioLabelText>
                        </Styled.RadioLabel>
                      </Styled.InputWrapper>
                    </Styled.Fieldset>
                    <Styled.InputMessage>
                      예약할 때의 시간 단위를 설정할 수 있습니다.
                    </Styled.InputMessage>
                  </Styled.FormRow>

                  <Styled.FormRow>
                    <Styled.InputWrapper>
                      <Input
                        type="number"
                        min="0"
                        max="1440"
                        step={reservationTimeUnit}
                        label="최소 예약 가능 시간 (분)"
                        value={reservationMinimumTimeUnit}
                        onChange={(event) =>
                          handleChangeReservationForm(event, onChangeReservationMinimumTimeUnit)
                        }
                        required
                      />
                      <Input
                        type="number"
                        min={reservationMinimumTimeUnit}
                        max="1440"
                        step={reservationTimeUnit}
                        label="최대 예약 가능 시간 (분)"
                        value={reservationMaximumTimeUnit}
                        onChange={(event) =>
                          handleChangeReservationForm(event, onChangeReservationMaximumTimeUnit)
                        }
                        required
                      />
                    </Styled.InputWrapper>
                    <Styled.InputMessage>
                      예약 가능한 최소 시간과 최대 시간을 설정할 수 있습니다.
                    </Styled.InputMessage>
                  </Styled.FormRow>

                  <Styled.FormRow>
                    <Styled.Fieldset>
                      <Styled.FormLabel>예약 가능한 요일</Styled.FormLabel>
                      <Styled.WeekdaySelect>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday>월</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={monday}
                            name="monday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday>화</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={tuesday}
                            name="tuesday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday>수</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={wednesday}
                            name="wednesday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday>목</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={thursday}
                            name="thursday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday>금</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={friday}
                            name="friday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday value="Saturday">토</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={saturday}
                            name="saturday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                        <Styled.WeekdayLabel>
                          <Styled.Weekday value="Sunday">일</Styled.Weekday>
                          <input
                            type="checkbox"
                            checked={sunday}
                            name="sunday"
                            onChange={(event) =>
                              handleChangeReservationForm(event, handleChangeEnabledDayOfWeek)
                            }
                          />
                        </Styled.WeekdayLabel>
                      </Styled.WeekdaySelect>
                    </Styled.Fieldset>
                  </Styled.FormRow>
                  <Styled.FormRow>
                    {isAddingSpace ? (
                      <Styled.FormSubmitContainer>
                        <Button type="button" variant="text" onClick={handleCancelForm}>
                          취소
                        </Button>
                        <Button variant="primary">공간 추가</Button>
                      </Styled.FormSubmitContainer>
                    ) : (
                      <Styled.FormSubmitContainer>
                        <Styled.DeleteButton
                          type="button"
                          variant="text"
                          onClick={handleDeleteSpace}
                        >
                          <DeleteIcon />
                          공간 삭제
                        </Styled.DeleteButton>
                        <Button variant="primary">저장</Button>
                      </Styled.FormSubmitContainer>
                    )}
                  </Styled.FormRow>
                </Styled.Form>
              ) : (
                <Styled.NoSpaceMessage>공간을 선택해주세요</Styled.NoSpaceMessage>
              )}
            </Styled.FormContainer>
          </Styled.EditorContainer>
        </Styled.Page>
      </Layout>
      <Modal
        open={isPresetFormOpen}
        isClosableDimmer
        showCloseButton
        onClose={() => setPresetFormOpen(false)}
      >
        <Modal.Header>추가할 프리셋의 이름을 입력해주세요</Modal.Header>
        <Modal.Inner>
          <form onSubmit={handleSubmitPreset}>
            <Input
              type="text"
              maxLength={SPACE.PRESET_NAME.MAX_LENGTH}
              value={presetName}
              onChange={onChangePresetName}
              message={createPreset.error?.response?.data.message ?? ''}
              status={!!createPreset.error?.response?.data.message ? 'error' : 'default'}
              autoFocus
            />
            <Styled.PresetNameFormControl>
              <Button variant="text">저장</Button>
            </Styled.PresetNameFormControl>
          </form>
        </Modal.Inner>
      </Modal>
    </>
  );
};

export default ManagerSpaceEdit;
