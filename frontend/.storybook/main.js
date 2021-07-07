const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

module.exports = {
  stories: ['../src/**/*.stories.mdx', '../src/**/*.stories.@(js|jsx|ts|tsx)'],
  addons: ['@storybook/addon-links', '@storybook/addon-essentials'],
  typescript: {
    check: false,
    checkOptions: {},
    reactDocgen: false,
  },
  webpackFinal: (config) => {
    const newConfig = {
      ...config,
      module: {
        ...config.module,
        rules: [
          {
            test: /\.svg$/,
            use: ['@svgr/webpack'],
          },
          ...config.module.rules,
        ],
      },
    };

    newConfig.resolve.plugins.push(new TsconfigPathsPlugin({}));
    return newConfig;
  },
};
