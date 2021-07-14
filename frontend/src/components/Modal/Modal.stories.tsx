import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import styled from 'styled-components';
import Input from 'components/Input/Input';
import Modal, { Props } from './Modal';

export default {
  title: 'shared/Modal',
  component: Modal,
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Modal {...args} />;

export const Default = Template.bind({});
Default.args = {
  open: true,
  children: <h1>Inner에 들어가있지 않은 콘텐츠입니다.(패딩 X)</h1>,
};

export const Inner = Template.bind({});
Inner.args = {
  open: true,
  children: (
    <Modal.Inner>
      <h1>Inner에 들어있는 콘텐츠입니다.(패딩 O)</h1>
    </Modal.Inner>
  ),
};

const Container = styled.div`
  display: flex;
  justify-content: space-between;
`;

const Button = styled.button`
  border: none;
  cursor: pointer;
  margin-left: 1rem;
  background-color: white;

  &:hover {
    opacity: 0.7;
  }
`;

export const PasswordInput = Template.bind({});
PasswordInput.args = {
  open: true,
  children: (
    <>
      <Modal.Header>예약시 사용하신 비밀번호를 입력해주세요.</Modal.Header>
      <Modal.Inner>
        <Input type="password" label="비밀번호" minLength={4} maxLength={4} />
        <Container>
          <div></div>
          <div>
            <Button>취소</Button>
            <Button>확인</Button>
          </div>
        </Container>
      </Modal.Inner>
    </>
  ),
};

const Box = styled.div`
  display: flex;
  flex-direction: column;
  width: 80vw;
`;

const SelectButton = styled.button`
  border: none;
  cursor: pointer;
  background-color: white;
  padding: 1rem 1.5rem;
  text-align: left;
  font-size: 1.25rem;

  &:hover {
    opacity: 0.7;
  }

  &:first-child {
    border-bottom: 1px solid rgba(196, 196, 196, 0.3);
  }
`;

export const Select = Template.bind({});
Select.args = {
  open: true,
  children: (
    <>
      <Box>
        <SelectButton>🛠 수정하기</SelectButton>
        <SelectButton>🗑 삭제하기</SelectButton>
      </Box>
    </>
  ),
};

export const CloseButton = Template.bind({});
CloseButton.args = {
  open: true,
  showCloseButton: true,
  children: (
    <>
      <Modal.Header>Ipsum Lorem</Modal.Header>
      <Modal.Inner>
        <p>
          Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has
          been the industry's standard dummy text ever since the 1500s, when an unknown printer took
          a galley of type and scrambled it to make a type specimen book. It has survived not only
          five centuries, but also the leap into electronic typesetting, remaining essentially
          unchanged. It was popularised in the 1960s with the release of Letraset sheets containing
          Lorem Ipsum passages, and more recently with desktop publishing software like Aldus
          PageMaker including versions of Lorem Ipsum.
        </p>
      </Modal.Inner>
    </>
  ),
};
