import THROW_ERROR from 'constants/throwError';

export const getLocalStorageItem = <T = unknown>({
  key,
  defaultValue,
}: {
  key: string;
  defaultValue: T;
}): T => {
  const storedData = localStorage.getItem(key);

  if (!storedData) {
    return defaultValue;
  }

  try {
    return JSON.parse(storedData) as T;
  } catch {
    throw new Error(THROW_ERROR.NOT_JSON_FORMAT);
  }
};

const getCircularReplacer = () => {
  const seen = new WeakSet();

  return (key: string, value: unknown) => {
    if (typeof value === 'object' && value !== null) {
      if (seen.has(value)) {
        return;
      }
      seen.add(value);
    }
    return value;
  };
};

export const setLocalStorageItem = ({ key, item }: { key: string; item: unknown }): void => {
  const data = JSON.stringify(item, getCircularReplacer());

  if (data === undefined) {
    throw new Error(THROW_ERROR.NOT_MATCHED_JSON);
  }

  localStorage.setItem(key, data);
};

export const removeLocalStorageItem = ({ key }: { key: string }): void => {
  localStorage.removeItem(key);
};
