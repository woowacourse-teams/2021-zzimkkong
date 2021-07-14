const REGEXP = {
  PASSWORD: /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$/,
  ORGANIZATION: /^[a-zA-Z0-9ㄱ-ㅎ가-힣ㅏ-ㅣ-_!?.,\s]{1,}$/,
};

export default REGEXP;
