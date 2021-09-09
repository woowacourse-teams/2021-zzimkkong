const MESSAGE = {
  JOIN: {
    SUCCESS: '회원가입에 성공했습니다.',
    FAILURE: '회원가입에 실패했습니다.',
    VALID_EMAIL: '사용 가능한 이메일입니다.',
    VALID_PASSWORD: '사용 가능한 비밀번호입니다.',
    INVALID_PASSWORD: '영어와 숫자를 포함하여 8~20자로 입력해주세요.',
    VALID_PASSWORD_CONFIRM: '비밀번호가 일치합니다.',
    INVALID_PASSWORD_CONFIRM: '비밀번호가 서로 다릅니다.',
    VALID_ORGANIZATION: '유효한 조직명입니다.',
    INVALID_ORGANIZATION: '특수문자는 _ . , ! ? 만 허용됩니다.',
    UNEXPECTED_ERROR: '이메일 중복 확인에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
  LOGIN: {
    UNEXPECTED_ERROR: '로그인에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
  RESERVATION: {
    CREATE: '예약하기',
    EDIT: '예약 수정하기',
    SUGGESTION: '오늘의 첫 예약을 잡아보세요!',
    PENDING: '불러오는 중입니다...',
    ERROR: `예약 목록을 불러오는 데 문제가 생겼어요!\n새로 고침으로 다시 시도해주세요.`,
    DELETE_SUCCESS: '예약이 삭제 되었습니다.',
    UNEXPECTED_ERROR: '예약하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    UNEXPECTED_DELETE_ERROR:
      '예약을 삭제하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    INVALID_MAP_ID: '맵 ID가 올바르지 않습니다. 다시 확인해주세요.',
    PASSWORD_MESSAGE: '숫자 4자리를 입력해주세요.',
  },
  MANAGER_MAIN: {
    UNEXPECTED_GET_DATA_ERROR:
      '정보를 불러오는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    UNEXPECTED_MAP_DELETE_ERROR:
      '맵을 삭제하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    RESERVATION_DELETE: '예약이 삭제되었습니다.',
    RESERVATION_DELETE_CONFIRM: '예약을 삭제하시겠습니까?',
    MAP_DELETED: '맵이 삭제되었습니다.',
    SELECT_MAP: '맵을 선택해주세요.',
    COPIED_SHARE_LINK: '맵의 공유링크가 클립보드에 복사되었습니다!',
    UNEXPECTED_COPY_SHARE_LINK: '공유링크를 복사하는 데 문제가 발생했습니다.',
  },
  MANAGER_SPACE: {
    GET_UNEXPECTED_ERROR:
      '공간 데이터를 불러오는 데 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    ADD_UNEXPECTED_ERROR: '공간을 생성하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    EDIT_UNEXPECTED_ERROR:
      '공간 설정을 수정하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    DELETE_UNEXPECTED_ERROR:
      '공간을 삭제하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    CANCEL_ADD_SPACE_CONFIRM: '입력한 공간 설정은 저장되지 않습니다. 공간 추가를 취소하시겠습니까?',
    DELETE_SPACE_CONFIRM: '공간을 삭제하시겠습니까?',
    ADD_PRESET_UNEXPECTED_ERROR:
      '프리셋을 추가하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    DELETE_PRESET_UNEXPECTED_ERROR:
      '프리셋을 삭제하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
    SPACE_CREATED: '공간이 생성되었습니다.',
    SPACE_SETTING_UPDATED: '공간 설정이 수정되었습니다.',
    SPACE_DELETED: '공간이 생성되었습니다.',
    PRESET_CREATED: '프리셋이 추가되었습니다.',
    PRESET_DELETED: '프리셋이 삭제되었습니다.',
    DELETE_PRESET_CONFIRM: '이 프리셋을 삭제하시겠어요?',
  },
  MANAGER_MAP: {
    CREATE_SUCCESS_CONFIRM: '맵 생성 완료! 공간을 편집하러 가시겠어요?',
    UPDATE_SUCCESS: '맵이 수정되었습니다.',
    CANCEL_CONFIRM: '편집 중인 맵은 저장되지 않으며, 메인 페이지로 돌아갑니다.',
    UNEXPECTED_MAP_CREATE_ERROR:
      '맵을 생성하는 중에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
  },
};

export default MESSAGE;
