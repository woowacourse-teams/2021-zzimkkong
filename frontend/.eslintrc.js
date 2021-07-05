module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  ignorePatterns: ['*.js'],
  extends: ['react-app', 'prettier'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 12,
    sourceType: 'module',
    project: './tsconfig.json',
  },
  plugins: [],
  rules: {},
};
