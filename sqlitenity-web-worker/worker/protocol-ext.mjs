// @ts-check

import { assertNever, errorMessage } from "./utils.mjs";
import { isLiteral, isShape, isType, isUnion } from "./validation.mjs";

/**
 * @typedef {import("./typed-sqlite3.mjs").Context} Context
 * @typedef {import("./typed-sqlite3.mjs").ContextOrError} ContextOrError
 *
 * @typedef {{ cmd: "delete", prefix: string }} DeleteCommand
 * @typedef {DeleteCommand | { cmd: never }} Command
 *
 * @typedef {{ deletedFiles: string[] }} DeleteResponseData
 * @typedef {DeleteResponseData} ResponseData
 *
 * @typedef {{ data: Command }} WorkerRequest
 * @typedef {{ data: ResponseData }} WorkerSuccessResponse
 * @typedef {{ error: string }} WorkerErrorResponse
 * @typedef {WorkerSuccessResponse | WorkerErrorResponse} WorkerResponse
 */

const isDeleteCommand = isShape({
  cmd: isLiteral("delete"),
  prefix: isType("string"),
});

const isCommand = isUnion(isDeleteCommand);

export const isMessageExt = isShape({
  data: isShape({
    data: isCommand,
  }),
});

/**
 *
 * @param {ContextOrError} context
 * @param {MessageEvent<WorkerRequest>} message
 */
export function handleMessageExt(context, message) {
  postResponseToPorts(executeRequest(context, message.data), message.ports);
}

/**
 *
 * @param {WorkerResponse} response
 * @param {readonly MessagePort[]} ports
 */
function postResponseToPorts(response, ports) {
  if (response) {
    for (const port of ports) {
      port.postMessage(response);
    }
  }
}

/**
 *
 * @param {ContextOrError} context
 * @param {WorkerRequest} request
 * @returns {WorkerResponse}
 */
function executeRequest(context, request) {
  if ("error" in context) {
    return { error: context.error };
  }

  try {
    return tryExecuteRequest(context.context, request);
  } catch (error) {
    return { error: errorMessage(error) };
  }
}

/**
 *
 * @param {Context} context
 * @param {WorkerRequest} request
 * @returns {WorkerSuccessResponse}
 */
function tryExecuteRequest(context, request) {
  return { data: tryExecuteCommand(context, request.data) };
}

/**
 *
 * @param {Context} context
 * @param {Command} command
 * @returns {ResponseData}
 */
function tryExecuteCommand(context, command) {
  switch (command.cmd) {
    case "delete":
      return tryExecuteDeleteCommand(context, command);
    default:
      return assertNever(command);
  }
}

/**
 *
 * @param {Context} context
 * @param {DeleteCommand} command
 * @returns {DeleteResponseData}
 */
function tryExecuteDeleteCommand(context, command) {
  const { pool } = context;
  const { prefix } = command;

  validateCanonicalOpfsPrefix(prefix);

  const fileNames = pool.getFileNames();

  for (const fileName of fileNames) {
    if (fileName.startsWith(prefix)) {
      if (!pool.unlink(fileName)) {
        throw new Error(`internal error: could not unlink ${fileName}`);
      }
    }
  }

  return { deletedFiles: fileNames };
}

/**
 *
 * @param {string} prefix
 */
function validateCanonicalOpfsPrefix(prefix) {
  if (!prefix.startsWith("/")) {
    throw new Error(`Prefix must be an absolute string starting with "/".`);
  }

  if (prefix === "/") {
    return;
  }

  if (prefix.endsWith("/")) {
    throw new Error("Canonical path should not end with a trailing slash.");
  }

  const segments = prefix.slice(1).split("/");

  for (const segment of segments) {
    if (segment === "" || segment === "." || segment === ".." || segment.includes("\0")) {
      throw new Error(`Invalid or non-canonical path segment: "${segment}".`);
    }
  }
}
