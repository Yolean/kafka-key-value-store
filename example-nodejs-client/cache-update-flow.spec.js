
const {
  PIXY_HOST = 'localhost:18090',
  CACHE1_HOST = 'localhost:18081'
} = process.env;

const request = require('request');

const mockserver = require('./mockserver');

beforeAll(() => {
  mockserver.start();
});

afterAll(() => {
  mockserver.stop();
});

describe("A complete cache update flow", () => {

  test("Check that the mock server is online", done => {

    request(mockserver.localroot, function (error, response, body) {
      console.log('error:', error); // Print the error if one occurred
      console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
      console.log('body:', body); // Print the HTML for the Google homepage.
      done();
    });

  });

  test("Check that pixy is online", done => {

    request(`http://${PIXY_HOST}`, function (error, response, body) {
      console.log('error:', error); // Print the error if one occurred
      console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
      console.log('body:', body); // Print the HTML for the Google homepage.
      done();
    });

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
