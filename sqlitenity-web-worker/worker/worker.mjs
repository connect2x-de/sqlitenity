// @ts-check

import { createContext } from "./typed-sqlite3.mjs";
import { localSQLite, errorMessage } from "./utils.mjs";
import { handleMessage } from "./protocol.mjs";
import { handleMessageExt, isMessageExt } from "./protocol-ext.mjs";

async function initialize() {
  try {
    await tryInitialize();
  } catch (error) {
    self.postMessage({ error: `Failed to initialize SQLite3 Web Worker: ${errorMessage(error)}` });
  }
}

async function tryInitialize() {
  /** @type {MessageEvent<unknown>[]} */
  const messageQueue = [];
  self.onmessage = (event) => messageQueue.push(event);

  const context = await createContext({ locateFile: localSQLite });

  /** @param {MessageEvent<unknown>} event */
  function onEvent(event) {
    if (isMessageExt(event)) {
      handleMessageExt(context, event);
    } else {
      handleMessage(context, event);
    }
  }

  for (const event of messageQueue) {
    onEvent(event);
  }

  self.onmessage = onEvent;
}

void initialize();
