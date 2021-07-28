import { useState, useRef, useEffect } from 'react';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as ItemsIcon } from 'assets/svg/items.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as PolylineIcon } from 'assets/svg/polyline.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';

import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import PALETTE from 'constants/palette';
import useInput from 'hooks/useInput';
import * as Styled from './ManagerMapCreate.styles';

const GRID_SIZE = 10;

const ManagerMapCreate = (): JSX.Element => {
  const editorRef = useRef<HTMLDivElement | null>(null);

  const [widthValue, onChangeWidthValue] = useInput('800');
  const [heightValue, onChangeHeightValue] = useInput('600');

  const width = Number(widthValue);
  const height = Number(heightValue);

  const [board, setBoard] = useState({
    x: 0,
    y: 0,
    scale: 1,
  });

  useEffect(() => {
    const editorWidth = editorRef.current ? editorRef.current.offsetWidth : 0;
    const editorHeight = editorRef.current ? editorRef.current.offsetHeight : 0;

    setBoard((prevBoard) => ({
      ...prevBoard,
      x: (editorWidth - width) / 2,
      y: (editorHeight - height) / 2,
    }));
  }, [width, height]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.EditorHeader>
            <Styled.HeaderContent>
              <Styled.MapNameContainer>
                <Styled.MapName>루터회관 14층</Styled.MapName>
                <Styled.EditButton>
                  <EditIcon />
                </Styled.EditButton>
              </Styled.MapNameContainer>
              <Styled.TempSaveContainer>
                <Styled.TempSaveMessage>1분 전에 임시 저장되었습니다.</Styled.TempSaveMessage>
                <Styled.TempSaveButton variant="primary-text">임시 저장</Styled.TempSaveButton>
              </Styled.TempSaveContainer>
            </Styled.HeaderContent>
            <Styled.HeaderContent>
              <Styled.ButtonContainer>
                <Button variant="text">취소</Button>
                <Button variant="primary">완료</Button>
              </Styled.ButtonContainer>
            </Styled.HeaderContent>
          </Styled.EditorHeader>
          <Styled.EditorContent>
            <Styled.Toolbar>
              <IconButton text="선택">
                <SelectIcon />
              </IconButton>
              <IconButton text="이동">
                <MoveIcon />
              </IconButton>
              <IconButton text="선">
                <LineIcon />
              </IconButton>
              <IconButton text="다각선">
                <PolylineIcon />
              </IconButton>
              <IconButton text="장식">
                <ItemsIcon />
              </IconButton>
            </Styled.Toolbar>
            <Styled.Editor ref={editorRef}>
              <svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="100%" height="100%">
                <rect width="100%" height="100%" fill={PALETTE.GRAY[200]}></rect>
                <svg xmlns="http://www.w3.org/2000/svg" version="1.1">
                  <defs>
                    <pattern
                      id="smallGrid"
                      width={`${GRID_SIZE}px`}
                      height={`${GRID_SIZE}px`}
                      patternUnits="userSpaceOnUse"
                    >
                      <path
                        d={`M ${GRID_SIZE} 0 L 0 0 0 ${GRID_SIZE}`}
                        fill="none"
                        stroke="gray"
                        strokeWidth="0.5"
                      />
                    </pattern>
                    <pattern
                      id="grid"
                      width={`${GRID_SIZE * 10}px`}
                      height={`${GRID_SIZE * 10}px`}
                      patternUnits="userSpaceOnUse"
                    >
                      <rect
                        width={`${GRID_SIZE * 10}px`}
                        height={`${GRID_SIZE * 10}px`}
                        fill="url(#smallGrid)"
                      />
                      <path
                        d={`M ${GRID_SIZE * 10} 0 L 0 0 0 ${GRID_SIZE * 10}`}
                        fill="none"
                        stroke="gray"
                        strokeWidth="1"
                      />
                    </pattern>
                  </defs>
                  <g
                    id="board"
                    transform={`matrix(${board.scale}, 0, 0, ${board.scale}, ${board.x}, ${board.y})`}
                  >
                    <rect width={`${width}px`} height={`${height}px`} fill="white" />
                    {/* 전체 격자를 그리는 rect */}
                    <rect
                      width={`${width + 0.5}px`}
                      height={`${height + 0.5}px`}
                      fill="url(#grid)"
                    />
                  </g>
                </svg>
              </svg>
            </Styled.Editor>
            <Styled.Toolbar>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>W</Styled.LabelIcon>
                  <Styled.LabelText>넓이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput value={widthValue} onChange={onChangeWidthValue} />
              </Styled.InputWrapper>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>H</Styled.LabelIcon>
                  <Styled.LabelText>높이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput value={heightValue} onChange={onChangeHeightValue} />
              </Styled.InputWrapper>
            </Styled.Toolbar>
          </Styled.EditorContent>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerMapCreate;
