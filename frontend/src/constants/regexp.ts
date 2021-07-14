const REGEXP = {
  PASSWORD: /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$/,
  RESERVATION_PASSWORD: /^[0-9]{4}$/,
};

export default REGEXP;
