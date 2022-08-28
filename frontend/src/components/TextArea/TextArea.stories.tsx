import { Story } from '@storybook/react';
import React, { useState } from 'react';
import TextArea, { Props } from './TextArea';

export default {
  title: 'shared/TextArea',
  component: TextArea,
};

const Template: Story<Props> = (args) => {
  const [value, setValue] = useState('');

  const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) =>
    setValue(event.target.value);

  return <TextArea {...args} value={value} onChange={handleChange} />;
};

export const Default = Template.bind({});
Default.args = {
  label: '공지사항',
  maxLength: 100,
};
