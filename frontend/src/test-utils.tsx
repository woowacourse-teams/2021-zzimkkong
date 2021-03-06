import { render, RenderOptions, RenderResult } from '@testing-library/react';
import React, { Suspense } from 'react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { BrowserRouter as Router } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import AccessTokenProvider from 'providers/AccessTokenProvider';
import { GlobalStyle, theme } from './App.styles';

const queryClient = new QueryClient();

const AllTheProviders: React.FC = ({ children }): JSX.Element => (
  <QueryClientProvider client={queryClient}>
    <AccessTokenProvider>
      <ThemeProvider theme={theme}>
        <Suspense fallback={<div></div>}>
          <GlobalStyle />
          <Router>{children}</Router>
        </Suspense>
      </ThemeProvider>
    </AccessTokenProvider>
  </QueryClientProvider>
);

const customRender = (
  ui: React.ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>
): RenderResult => render(ui, { wrapper: AllTheProviders, ...options });

export * from '@testing-library/react';
export { customRender as render };
