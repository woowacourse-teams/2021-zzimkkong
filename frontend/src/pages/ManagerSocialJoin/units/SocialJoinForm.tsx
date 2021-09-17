import { FormEventHandler, useEffect, useState } from 'react';
import Input from 'components/Input/Input';
import SocialJoinButton from 'components/SocialAuthButton/SocialJoinButton';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import useInput from 'hooks/useInput';
import { SocialJoinParams } from '../ManagerSocialJoin';
import * as Styled from './SocialJoinForm.styles';

interface Props {
  email: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
  onSubmit: ({ email, organization }: SocialJoinParams) => void;
}

const SocialJoinForm = ({ email, oauthProvider, onSubmit }: Props): JSX.Element => {
  const [organization, onChangeForm] = useInput('');

  const [organizationMessage, setOrganizationMessage] = useState('');

  const isValidOrganization = REGEXP.ORGANIZATION.test(organization);

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    onSubmit({ email, organization });
  };

  useEffect(() => {
    if (!organization) {
      setOrganizationMessage('');

      return;
    }

    setOrganizationMessage(
      isValidOrganization ? MESSAGE.JOIN.VALID_ORGANIZATION : MESSAGE.JOIN.INVALID_ORGANIZATION
    );
  }, [organization, isValidOrganization]);

  return (
    <Styled.Form onSubmit={handleSubmit}>
      <Input
        type="email"
        label="이메일"
        name="email"
        value={email}
        onChange={onChangeForm}
        required
        disabled
      />
      <Input
        type="text"
        label="조직명"
        name="organization"
        minLength={MANAGER.ORGANIZATION.MIN_LENGTH}
        value={organization}
        onChange={onChangeForm}
        message={organizationMessage}
        status={isValidOrganization ? 'success' : 'error'}
        required
        autoFocus
      />
      <SocialJoinButton provider={oauthProvider} />
    </Styled.Form>
  );
};

export default SocialJoinForm;
