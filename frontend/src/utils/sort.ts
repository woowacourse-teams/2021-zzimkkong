import { ManagerSpace, Order, SpaceReservation } from 'types/common';

export const sortReservations = (
  reservations: SpaceReservation[],
  order: Order = Order.Ascending
): SpaceReservation[] =>
  reservations.sort((a, b) => {
    if (a.spaceColor !== b.spaceColor) {
      if (order === Order.Ascending) return a.spaceColor < b.spaceColor ? -1 : 1;

      return a.spaceColor > b.spaceColor ? -1 : 1;
    }

    const aSpaceNameWithoutWhitespace = a.spaceName.replaceAll(' ', '');
    const bSpaceNameWithoutWhitespace = b.spaceName.replaceAll(' ', '');

    if (order === Order.Ascending)
      return aSpaceNameWithoutWhitespace < bSpaceNameWithoutWhitespace ? -1 : 1;

    return aSpaceNameWithoutWhitespace > bSpaceNameWithoutWhitespace ? -1 : 1;
  });

export const sortSpaces = (
  spaces: ManagerSpace[],
  order: Order = Order.Ascending
): ManagerSpace[] =>
  spaces.sort((a, b) => {
    if (a.color !== b.color) {
      if (order === Order.Ascending) return a.color < b.color ? -1 : 1;

      return a.color > b.color ? -1 : 1;
    }

    const aSpaceNameWithoutWhitespace = a.name.replaceAll(' ', '');
    const bSpaceNameWithoutWhitespace = b.name.replaceAll(' ', '');

    if (order === Order.Ascending)
      return aSpaceNameWithoutWhitespace < bSpaceNameWithoutWhitespace ? -1 : 1;

    return aSpaceNameWithoutWhitespace > bSpaceNameWithoutWhitespace ? -1 : 1;
  });
