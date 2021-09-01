import server from './server';

global.scrollTo = jest.fn();
global.alert = jest.fn();
window.alert = jest.fn();

beforeAll(() => server.listen());

afterEach(() => server.resetHandlers());

afterAll(() => server.close());
