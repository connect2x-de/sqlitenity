// @ts-check

/**
 * @typedef {import("./validation.mjs").Validated<typeof isSQLiteValue>} Value
 *
 * @typedef {object} DatabaseOptions
 * @property {string} filename
 * @property {string} flags
 * @property {string} vfs
 *
 * @typedef {object} Statement
 * @property {number} columnCount
 * @property {(index: number, value: Value) => Statement} bind
 * @property {() => Statement} clearBindings
 * @property {() => Statement} reset
 * @property {() => boolean} step
 * @property {(target: Value[]) => Value[]} get
 * @property {() => number | undefined} finalize
 *
 * @typedef {object} Database
 * @property {(sql: string) => Statement} prepare
 * @property {() => void} close
 *
 * @typedef {object} Pool
 * @property {(min: number) => Promise<number>} reserveMinimumCapacity
 * @property {() => number} getCapacity
 * @property {() => number} getFileCount
 * @property {() => string[]} getFileNames
 * @property {(path: string) => boolean} unlink
 *
 * @typedef {new (options: DatabaseOptions) => Database} DatabaseConstructor
 *
 * @typedef {object} Module
 * @property {{ DB: DatabaseConstructor }} oo1
 * @property {{
 *   sqlite3_bind_parameter_count: (statement: Statement) => number,
 *   sqlite3_column_name: (statement: Statement, columnIndex: number) => string,
 *   sqlite3_column_type: (statement: Statement, columnIndex: number) => number,
 * }} capi
 * @property {(options: { initialCapacity: number }) => Promise<Pool>} installOpfsSAHPoolVfs
 *
 * @typedef {{ locateFile: (path: string) => string }} InitializerOptions
 * @typedef {(options: InitializerOptions) => Promise<Module>} Initializer
 *
 * @typedef {object} Context
 * @property {Module} sqlite3
 * @property {Pool} pool
 * @property {Map<number, Database>} databases
 * @property {Map<number, Statement>} statements
 * @property {number} nextDatabaseId
 * @property {number} nextStatementId
 *
 * @typedef {{ context: Context } | { error: string }} ContextOrError
 */

import sqlite3InitModule from "./sqlite3.mjs";
import { errorMessage } from "./utils.mjs";
import { isUnion, isInstance, isLiteral, isType } from "./validation.mjs";

const initializeSqliteModule = /** @type {Initializer} */ (
  /** @type {unknown} */ (sqlite3InitModule)
);

export const isSQLiteValue = isUnion(
  isLiteral(null),
  isType("number"),
  isType("bigint"),
  isType("string"),
  isType("boolean"),
  isInstance(ArrayBuffer),
  isInstance(Uint8Array),
  isInstance(Int8Array),
);

/**
 * @param {InitializerOptions} initializer
 * @returns {Promise<ContextOrError>}
 */
export async function createContext(initializer) {
  try {
    return { context: await tryCreateContext(initializer) };
  } catch (error) {
    return { error: `Failed to initialize SQLite3 Web Worker: ${errorMessage(error)}` };
  }
}

/**
 * @param {InitializerOptions} initializer
 * @returns {Promise<Context>}
 */
async function tryCreateContext(initializer) {
  const sqlite3 = await initializeSqliteModule(initializer);
  const pool = await sqlite3.installOpfsSAHPoolVfs({
    initialCapacity: 100,
  });

  return {
    sqlite3,
    pool,
    databases: new Map(),
    statements: new Map(),
    nextDatabaseId: 0,
    nextStatementId: 0,
  };
}
