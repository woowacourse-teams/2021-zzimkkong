import React, { ComponentProps } from 'react';
import { theme } from 'App.styles';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import IconButton from 'components/IconButton/IconButton';
import MESSAGE from 'constants/message';
import { QueryManagerMapSuccess } from 'types/response';

interface Props extends ComponentProps<typeof IconButton> {
  map: QueryManagerMapSuccess;
}

const ShareLinkButton = ({ map, ...props }: Props): JSX.Element => {
  const handleClick = () => {
    navigator.clipboard
      .writeText(`${window.location.origin}/guest/${map.sharingMapId ?? ''}`)
      .then(() => {
        alert(MESSAGE.MANAGER_MAIN.COPIED_SHARE_LINK);
      })
      .catch(() => {
        alert(MESSAGE.MANAGER_MAIN.UNEXPECTED_COPY_SHARE_LINK);
      });
  };

  return (
    <IconButton {...props}>
      <LinkIcon width="100%" height="100%" fill={theme.primary[500]} onClick={handleClick} />
    </IconButton>
  );
};

export default ShareLinkButton;
