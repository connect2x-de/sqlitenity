// @ts-check

/**
 * @typedef {import("./typed-sqlite3.mjs").Value} SQLiteValue
 * @typedef {import("./typed-sqlite3.mjs").Statement} SQLiteStatement
 * @typedef {import("./typed-sqlite3.mjs").Context} Context
 * @typedef {import("./typed-sqlite3.mjs").ContextOrError} ContextOrError

 * @typedef {{ cmd: "open", fileName: string }} OpenCommand
 * @typedef {{ cmd: "prepare", databaseId: number, sql: string }} PrepareCommand
 * @typedef {{ cmd: "step", statementId: number, bindings: SQLiteValue[] }} StepCommand
 * @typedef {{ cmd: "close", statementId: number, databaseId: null }} StatementCloseCommand
 * @typedef {{ cmd: "close", statementId: null, databaseId: number }} DatabaseCloseCommand
 * @typedef {OpenCommand | PrepareCommand | StepCommand | StatementCloseCommand | DatabaseCloseCommand} Command
 * 
 * @typedef {{ databaseId: number }} OpenResponse
 * @typedef {{ statementId: number, parameterCount: number, columnNames: string[] }} PrepareResponse
 * @typedef {{ columnTypes: number[], rows: SQLiteValue[][] }} StepResponse
 * @typedef {OpenResponse | PrepareResponse | StepResponse | void} ResponseData
 *
 * @typedef {{ id: number }} WithId
 *
 * @typedef {{ id: number, data: ResponseData } | void} SuccessResponse
 * @typedef {{ id: number, error: string }} ErrorResponse
 * @typedef {{ id?: number, error: string }} InvalidRequestErrorResponse

 * @typedef {{ id: number, data: Command }} WorkerRequest
 * @typedef {SuccessResponse | ErrorResponse | InvalidRequestErrorResponse} WorkerResponse
 */

import { isSQLiteValue } from "./typed-sqlite3.mjs";
import { assertNever, errorMessage } from "./utils.mjs";
import { isUnion, isList, isLiteral, isShape, isType, isIntersection } from "./validation.mjs";

const isOpenCommand = isShape({
  cmd: isLiteral("open"),
  fileName: isType("string"),
});

const isPrepareCommand = isShape({
  cmd: isLiteral("prepare"),
  databaseId: isType("number"),
  sql: isType("string"),
});

const isStepCommand = isShape({
  cmd: isLiteral("step"),
  statementId: isType("number"),
  bindings: isList(isSQLiteValue),
});

const isStatementCloseCommand = isShape({
  cmd: isLiteral("close"),
  statementId: isType("number"),
  databaseId: isLiteral(null),
});

const isDatabaseCloseCommand = isShape({
  cmd: isLiteral("close"),
  statementId: isLiteral(null),
  databaseId: isType("number"),
});

const isCommand = isUnion(
  isOpenCommand,
  isPrepareCommand,
  isStepCommand,
  isStatementCloseCommand,
  isDatabaseCloseCommand,
);

const hasId = isShape({ id: isType("number") });
const hasCommand = isShape({ data: isCommand });

const isWorkerRequest = isIntersection(hasId, hasCommand);

/**
 *
 * @param {ContextOrError} context
 * @param {MessageEvent<unknown>} message
 */
export function handleMessage(context, message) {
  if (isWorkerRequest(message.data)) {
    postResponse(executeRequest(context, message.data));
  } else if (!hasId(message.data)) {
    postResponse({ error: `invalid request: missing id: ${JSON.stringify(message.data)}` });
  } else {
    postResponse({
      id: message.data.id,
      error: `unknown command: ${JSON.stringify(message.data)}`,
    });
  }
}

/**
 *
 * @param {WorkerResponse} response
 */
function postResponse(response) {
  if (response) {
    self.postMessage(response);
  }
}

/**
 * @param {ContextOrError} context
 * @param {WorkerRequest} request
 * @returns {SuccessResponse | ErrorResponse}
 */
function executeRequest(context, request) {
  if ("error" in context) {
    return { id: request.id, error: context.error };
  }

  try {
    return tryExecuteRequest(context.context, request);
  } catch (error) {
    return { id: request.id, error: errorMessage(error) };
  }
}

/**
 * @param {Context} context
 * @param {WorkerRequest} request
 * @returns {SuccessResponse}
 */
function tryExecuteRequest(context, request) {
  return success(request.id, tryExecuteCommand(context, request.data));
}

/**
 * @param {number} id
 * @param {ResponseData} data
 * @returns {SuccessResponse}
 */
function success(id, data) {
  if (data) {
    return { id, data };
  }
}

/**
 * @param {Context} context
 * @param {Command} command
 * @returns {ResponseData}
 */
function tryExecuteCommand(context, command) {
  switch (command.cmd) {
    case "open":
      return tryOpenDatabase(context, command);
    case "prepare":
      return tryPrepareStatement(context, command);
    case "step":
      return tryStepStatement(context, command);
    case "close":
      return tryClose(context, command);
    default:
      return assertNever(command);
  }
}

/**
 * @param {Context} context
 * @param {OpenCommand} command
 * @returns {OpenResponse}
 */
function tryOpenDatabase(context, command) {
  if (!command.fileName.startsWith("/") && command.fileName !== ":memory:") {
    throw new Error("database path has to be absolute or :memory:");
  }

  const database = new context.sqlite3.oo1.DB({
    filename: command.fileName,
    flags: "c",
    vfs: `multipleciphers-${context.pool.vfsName}`,
  });
  const databaseId = context.nextDatabaseId++;
  context.databases.set(databaseId, database);
  return { databaseId };
}

/**
 * @param {Context} context
 * @param {PrepareCommand} command
 * @returns {PrepareResponse}
 */
function tryPrepareStatement(context, command) {
  const database = context.databases.get(command.databaseId);
  if (!database) {
    throw new Error(`Invalid database ID: ${command.databaseId}`);
  }

  const statement = database.prepare(command.sql);
  const { parameterCount, columnNames } = tryGetStatementInfoOrFinalize(context, statement);

  const statementId = context.nextStatementId++;
  context.statements.set(statementId, statement);
  return { statementId, parameterCount, columnNames };
}

/**
 * @param {Context} context
 * @param {StepCommand} command
 * @returns {StepResponse}
 */
function tryStepStatement(context, command) {
  const statement = context.statements.get(command.statementId);
  if (!statement) {
    throw new Error(`Invalid statement ID: ${command.statementId}`);
  }

  statement.reset();
  statement.clearBindings();

  for (const [index, binding] of command.bindings.entries()) {
    statement.bind(index + 1, binding);
  }

  const columnTypes = [];
  const rows = [];
  while (statement.step()) {
    if (columnTypes.length === 0) {
      for (let index = 0; index < statement.columnCount; index++) {
        columnTypes.push(context.sqlite3.capi.sqlite3_column_type(statement, index));
      }
    }
    rows.push(statement.get([]));
  }

  return { columnTypes, rows };
}

/**
 *
 * @param {Context} context
 * @param {StatementCloseCommand | DatabaseCloseCommand} command
 * @returns
 */
function tryClose(context, command) {
  return command.statementId === null
    ? tryCloseDatabase(context, command)
    : tryCloseStatement(context, command);
}

/**
 * @param {Context} context
 * @param {StatementCloseCommand} command
 */
function tryCloseStatement(context, command) {
  const statement = context.statements.get(command.statementId);
  if (!statement) {
    throw new Error(`Invalid statement ID: ${command.statementId}`);
  }

  statement.finalize();
  context.statements.delete(command.statementId);
}

/**
 * @param {Context} context
 * @param {DatabaseCloseCommand} command
 */
function tryCloseDatabase(context, command) {
  const database = context.databases.get(command.databaseId);
  if (!database) {
    throw new Error(`Invalid database ID: ${command.databaseId}`);
  }

  database.close();
  context.databases.delete(command.databaseId);
}

/**
 *
 * @param {Context} context
 * @param {SQLiteStatement} statement
 */
function tryGetStatementInfoOrFinalize(context, statement) {
  try {
    const parameterCount = context.sqlite3.capi.sqlite3_bind_parameter_count(statement);

    const columnNames = [];
    for (let index = 0; index < statement.columnCount; index++) {
      columnNames.push(context.sqlite3.capi.sqlite3_column_name(statement, index));
    }

    return { parameterCount, columnNames };
  } catch (error) {
    statement.finalize();
    throw error;
  }
}
