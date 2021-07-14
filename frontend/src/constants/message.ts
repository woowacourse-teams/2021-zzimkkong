const MESSAGE = {
  JOIN: {
    SUCCESS: '회원가입에 성공했습니다.',
    FAILURE: '회원가입에 실패했습니다.',
    VALID_EMAIL: '사용 가능한 이메일입니다.',
    VALID_PASSWORD: '사용 가능한 비밀번호입니다.',
    INVALID_PASSWORD: '영어와 숫자를 포함하여 8~20자로 입력해주세요.',
    VALID_PASSWORD_CONFIRM: '비밀번호가 일치합니다.',
    INVALID_PASSWORD_CONFIRM: '비밀번호가 서로 다릅니다.',
    UNEXPECTED_ERROR: '이메일 중복 확인에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
  LOGIN: {
    UNEXPECTED_ERROR: '로그인에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
  RESERVATION: {
    UNEXPECTED_ERROR: '예약하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
};

export default MESSAGE;
