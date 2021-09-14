import { ReactComponent as PlusSmallIcon } from 'assets/svg/plus-small.svg';
import Button from 'components/Button/Button';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';

interface Props {
  onClick: () => void;
}

const SpaceAddButton = ({ onClick }: Props): JSX.Element => {
  const { resetForm } = useFormContext(SpaceFormContext);

  const handleAddSpace = () => {
    onClick();
    resetForm();
  };

  return (
    <Button variant="primary" shape="round" onClick={handleAddSpace}>
      <PlusSmallIcon /> 공간 추가
    </Button>
  );
};

export default SpaceAddButton;
