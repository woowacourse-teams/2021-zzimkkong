import React from 'react';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import * as Styled from './ManagerMapCreate.styles';

const ManagerMapCreate = (): JSX.Element => {
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
              <EditIcon />
              <EditIcon />
              <EditIcon />
              <EditIcon />
            </Styled.Toolbar>
            <Styled.Editor></Styled.Editor>
            <Styled.Toolbar>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>W</Styled.LabelIcon>
                  <Styled.LabelText>넓이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput value="8000" />
              </Styled.InputWrapper>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>H</Styled.LabelIcon>
                  <Styled.LabelText>높이</Styled.LabelText>
                </Styled.Label>

                <Styled.SizeInput value="6000" />
              </Styled.InputWrapper>
            </Styled.Toolbar>
          </Styled.EditorContent>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerMapCreate;
