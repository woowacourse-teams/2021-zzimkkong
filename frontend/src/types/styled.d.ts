import 'styled-components';

type Color = string;

interface Palette {
  50?: Color;
  100?: Color;
  200?: Color;
  300?: Color;
  400?: Color;
  500?: Color;
  600?: Color;
  700?: Color;
  800?: Color;
  900?: Color;
}

declare module 'styled-components' {
  export interface DefaultTheme {
    primary: Palette;
    red: Palette;
    green: Palette;
    black: Palette;
    gray: Palette;
    white: Color;
  }
}
