import THROW_ERROR from 'constants/throwError';

export const getLocalStorageItem = ({
  key,
  defaultValue,
}: {
  key: string;
  defaultValue: unknown;
}): unknown => {
  const storedData = localStorage.getItem(key);

  if (!storedData) {
    return defaultValue;
  }

  try {
    return JSON.parse(storedData);
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
