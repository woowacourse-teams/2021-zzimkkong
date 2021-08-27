const hexToRgbArray = (hexColor: string) => {
  const shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;

  const hex = hexColor.replace(
    shorthandRegex,
    (_, r: string, g: string, b: string) => `#${r}${r}${g}${g}${b}${b}`
  );

  const hexR = parseInt(hex.slice(1, 3), 16);
  const hexG = parseInt(hex.slice(3, 5), 16);
  const hexB = parseInt(hex.slice(5, 7), 16);

  return [hexR, hexG, hexB];
};

const isHexColor = (colorCode: string) => {
  const regex = /^#(?:[0-9a-fA-F]{3}){1,2}$/;

  return regex.test(colorCode);
};

// ITU-R BT.709 formula
const getLuma = ([red, green, blue]: number[]) => 0.2126 * red + 0.7152 * green + 0.0722 * blue;

export const getTextColor = (backgroundColor: string): string => {
  if (!backgroundColor || backgroundColor === 'transparent') return 'black';

  if (isHexColor(backgroundColor)) {
    const rgb = hexToRgbArray(backgroundColor);
    const luma = getLuma(rgb);

    return luma < 255 / 2 ? 'white' : 'black';
  }

  const rgb = /^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/
    .exec(backgroundColor)
    ?.slice(1)
    .map((x) => Number(x));

  if (!rgb) {
    return 'black';
  }

  const luma = getLuma(rgb);

  return luma < 255 / 2 ? 'white' : 'black';
};
