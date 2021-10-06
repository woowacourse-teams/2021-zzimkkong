export enum Midday {
  AM = '오전',
  PM = '오후',
}
export interface Time {
  midday: Midday;
  hour: number;
  minute: number;
}
