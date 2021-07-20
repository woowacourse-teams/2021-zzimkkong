import { Story } from '@storybook/react';
import styled from 'styled-components';
import SpaceListItem, { Props } from './SpaceListItem';

export default {
  title: 'shared/SpaceListItem',
  component: SpaceListItem,
};

const Template: Story<Props> = (args) => <SpaceListItem {...args} />;

const Container = styled.div`
  display: flex;
  gap: 0.375rem;
  flex-wrap: wrap;
`;

const Wrapper = styled.div`
  width: calc(50% - (0.375rem / 2));
`;

const thumbnail = {
  src: './images/luther.png',
  alt: '루터회관 14F 공간',
};

export const Default = Template.bind({});
Default.args = {
  thumbnail,
  title: '루터회관 14F',
};

export const DoubleItem: Story<Props> = () => (
  <Container>
    <Wrapper>
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    </Wrapper>
    <Wrapper>
      <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
    </Wrapper>
  </Container>
);

export const TripleItem: Story<Props> = () => (
  <>
    <Container>
      <Wrapper>
        <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      </Wrapper>
      <Wrapper>
        <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      </Wrapper>
      <Wrapper>
        <SpaceListItem thumbnail={thumbnail} title="루터회관 14F" />
      </Wrapper>
    </Container>
  </>
);
