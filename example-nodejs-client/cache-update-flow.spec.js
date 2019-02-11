
const {
  PIXY_HOST = 'localhost:18090',
  CACHE1_HOST = 'localhost:18081'
} = process.env;

const fetch = require('node-fetch');

const mockserver = require('./mockserver');

beforeAll(() => {
  mockserver.start();
});

afterAll(() => {
  mockserver.stop();
});

describe("A complete cache update flow", () => {

  test("Check that the mock server is online", async () => {
    const response = await fetch(mockserver.localroot);
    expect(response.ok).toBeTruthy();
  });

  test("Check that pixy is online", async () => {
    const response = await fetch(`http://${PIXY_HOST}`);
    expect(response.status).toEqual(404);
  });

  it("Starts with a produce to Pixy", async () => {

  });

  it("Waits for ack from Pixy", async () => {

  });

  it("Gets the produced offset from Pixy's response", () => {

  });

  it("Waits for the cache to notify onUpdate", async () => {

  });

  it("When the notify handler returns non-200 gets another notify", async () => {

  });

  it("Includes the updated key and the offset at which the update happened", () => {

  });

  it("The message is now retrievable from KV using the key", async () => {

  });

  it("The current cache offset (global, not the key's latest) is avialable from KV", async () => {

  });

});
