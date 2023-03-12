import { useMemo, useState } from 'react';
import useEmojiList from 'hooks/query/useEmojiList';
import * as Styled from './EmojiSelector.styles';

interface EmojiSelectorProps {
  initialEmoji?: string;
  onSelect?: (emoji: string) => void;
}

const EmojiSelector = ({ initialEmoji, onSelect }: EmojiSelectorProps): JSX.Element => {
  const emojiListQuery = useEmojiList();

  const [selectedEmoji, setSelectedEmoji] = useState<string | null>(initialEmoji ?? null);

  const emojiList = useMemo(
    () => emojiListQuery.data?.data.emojis ?? [],
    [emojiListQuery.data?.data.emojis]
  );

  const handleSelect = (emoji: string) => {
    setSelectedEmoji(emoji);
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
              checked={selectedEmoji === emoji.name}
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
