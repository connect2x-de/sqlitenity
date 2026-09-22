// @ts-check

/**
 * @param {string} path
 * @returns {string}
 */
export function localSQLite(path) {
  if (path !== "sqlite3.wasm") throw new Error(`Unable to locate SQLite resource: ${path}`);
  return new URL("./sqlite3.wasm", import.meta.url).toString();
}

/**
 * @param {unknown} error
 * @returns {string}
 */
export function errorMessage(error) {
  return error instanceof Error ? error.message : String(error);
}

/**
 * @param {never} value
 * @returns {never}
 */
export function assertNever(value) {
  throw new Error(`Unhandled command: ${JSON.stringify(value)}`);
}
