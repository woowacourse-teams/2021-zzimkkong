import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import { EditorBoard } from 'types/common';

interface Props {
  width: number;
  height: number;
}

const useBoardStatus = ({
  width = 800,
  height = 600,
}: Props): [EditorBoard, Dispatch<SetStateAction<EditorBoard>>] => {
  const [boardStatus, setBoardStatus] = useState<EditorBoard>({
    scale: 1,
    x: 0,
    y: 0,
    width,
    height,
  });

  useEffect(() => {
    setBoardStatus((prevStatus) => ({
      ...prevStatus,
      width: Number(width),
      height: Number(height),
    }));
  }, [width, height]);

  return [boardStatus, setBoardStatus];
};

export default useBoardStatus;
