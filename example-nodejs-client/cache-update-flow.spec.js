
const {
  PIXY_HOST = 'http://localhost:18090',
  CACHE1_HOST = 'http://localhost:18081',
  TOPIC1_NAME = 'topic1',
  TEST_ID = '' + new Date().toISOString()
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

  test("Check that the mock server is online on port " + mockserver.port, async () => {
    const response = await fetch(mockserver.localroot);
    expect(response.ok).toBeTruthy();
  });

  test("Check that pixy is online", async () => {
    const response = await fetch(PIXY_HOST);
    expect(response.status).toEqual(404);
  });

  it("Starts with a produce to Pixy", async () => {
    const response = await fetch(`${PIXY_HOST}/topics/${TOPIC1_NAME}/messages`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({test: TEST_ID, n: 1})
    });
    expect(response.ok).toBeTruthy();
    expect(await response.json()).toEqual({});
  });

  let syncResponse = null;

  it("Waits for ack from Pixy", async () => {
    syncResponse = await fetch(`${PIXY_HOST}/topics/${TOPIC1_NAME}/messages?key=test1&sync`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({test: TEST_ID, n: 2})
    });
    expect(syncResponse.ok).toBeTruthy();
  });

  it("Gets the produced offset from Pixy's response", async () => {
    expect(syncResponse).toBeTruthy();
    const result = await syncResponse.json();
    expect(result).toBeTruthy();
    expect(result.partition).toEqual(0);
    expect(result.offset).toBeGreaterThan(1);
  });

  it("Until onUpdate is implemented we just have to wait here", done => {
    setTimeout(done, 900);
  });

  it("Finds the value in the cache", async () => {
    const response = await fetch(`${CACHE1_HOST}/messages/test1`);
    expect(await response.json()).toEqual({"key":"test1","value":`{\"test\":\"${TEST_ID}\",\"n\":2}`});
    expect(response.ok).toBeTruthy();
  });

  it("Waits for the cache to notify onUpdate", done => {
    // TODO for now we can see a log message that the cache service reaches, but detection/assertion here is TODO
    setTimeout(done, 4999);
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
