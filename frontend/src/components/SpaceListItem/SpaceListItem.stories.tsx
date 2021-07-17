import { Story } from '@storybook/react';
import styled from 'styled-components';
import SpaceListItem, { Props } from './SpaceListItem';

export default {
  title: 'shared/SpaceListItem',
  component: SpaceListItem,
};

const Template: Story<Props> = (args) => <SpaceListItem {...args} />;

const thumbnail = {
  src: './images/luther.png',
  alt: '루터회관 14F',
};

export const Default = Template.bind({});
Default.args = {
  thumbnail,
  title: '루터회관 14F',
};

const Container = styled.div`
  display: flex;
  gap: 0.375rem;
`;

const Wrapper = styled.div`
  width: calc(50% - (0.375rem / 2));
  margin-top: 0.5rem;
`;

export const SingleLineDouble: Story<Props> = () => (
  <Container>
    <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
  </Container>
);

export const SingleLineTriple: Story<Props> = () => (
  <>
    <Container>
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    </Container>
  </>
);

export const MultiLine: Story<Props> = () => (
  <>
    <Container>
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    </Container>
    <Wrapper>
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    </Wrapper>
  </>
);
