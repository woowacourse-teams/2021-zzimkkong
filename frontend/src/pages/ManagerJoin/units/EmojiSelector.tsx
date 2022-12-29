import { useMemo } from 'react';
import useEmojiList from 'hooks/query/useEmojiList';
import * as Styled from './EmojiSelector.styles';

interface EmojiSelectorProps {
  onSelect?: (emoji: string) => void;
}

const EmojiSelector = ({ onSelect }: EmojiSelectorProps): JSX.Element => {
  const emojiListQuery = useEmojiList();

  const emojiList = useMemo(
    () => emojiListQuery.data?.data.emojis ?? [],
    [emojiListQuery.data?.data.emojis]
  );

  const handleSelect = (emoji: string) => {
    onSelect?.(emoji);
  };

  return (
    <Styled.EmojiSelector>
      <Styled.LabelText>프로필 이모지 선택</Styled.LabelText>
      <Styled.EmojiList>
        {emojiList.map((emoji) => (
          <Styled.EmojiItem key={emoji.name}>
            <Styled.Radio
              type="radio"
              name="emoji"
              id={emoji.name}
              onChange={() => handleSelect(emoji.name)}
            />
            <Styled.EmojiCode>{emoji.code}</Styled.EmojiCode>
          </Styled.EmojiItem>
        ))}
      </Styled.EmojiList>
    </Styled.EmojiSelector>
  );
};

export default EmojiSelector;
