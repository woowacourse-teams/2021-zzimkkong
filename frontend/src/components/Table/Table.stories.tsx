import { Story } from '@storybook/react';
import Table, { Props } from './Table';

export default {
  title: 'shared/Table',
  component: Table,
};

const Template: Story<Props> = (args) => <Table {...args} />;

export const Default = Template.bind({});
Default.args = {
  children: (
    <>
      <Table.Head>
        <Table.Row>
          <Table.Header>이름</Table.Header>
          <Table.Header>주제</Table.Header>
          <Table.Header>시간</Table.Header>
          <Table.Header wordWrap>삭제</Table.Header>
        </Table.Row>
      </Table.Head>
      <Table.Body>
        <Table.Row>
          <Table.Cell wordWrap>체프</Table.Cell>
          <Table.Cell>맛있는 커피를 내리는 법</Table.Cell>
          <Table.Cell>13:00 - 15:00</Table.Cell>
          <Table.Cell></Table.Cell>
        </Table.Row>
        <Table.Row>
          <Table.Cell wordWrap>썬</Table.Cell>
          <Table.Cell>태양을 피하는 방법</Table.Cell>
          <Table.Cell>15:00 - 17:00</Table.Cell>
          <Table.Cell></Table.Cell>
        </Table.Row>
      </Table.Body>
    </>
  ),
};
