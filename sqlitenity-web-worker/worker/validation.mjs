// @ts-check

/**
 * @template T
 * @typedef {(value: unknown) => value is T} Validator
 */

/**
 * @template T
 * @typedef {{ [K in keyof T]: Validator<T[K]> }} ValidatorShape
 */

/**
 * @template {Validator<any>} V
 * @typedef {V extends Validator<infer T> ? T : never} Validated
 */

/**
 * @template T
 * @typedef {(T extends any ? (value: T) => void : never) extends
 *   ((value: infer I) => void) ? I : never} UnionToIntersection
 */

/**
 * @template T
 * @typedef {T extends "string" ? string :
 *   T extends "number" ? number :
 *   T extends "bigint" ? bigint :
 *   T extends "boolean" ? boolean :
 *   T extends "symbol" ? symbol :
 *   T extends "undefined" ? undefined :
 *   T extends "function" ? Function :
 *   object
 * } GetType
 */

/**
 * @template {Validator<any>[]} V
 * @param {V} validators
 */
export function isUnion(...validators) {
  /** @type {Validator<Validated<V[number]>>} */
  return (value) => validators.some((validator) => validator(value));
}

/**
 * @template {Validator<any>[]} V
 * @param {V} validators
 */
export function isIntersection(...validators) {
  /** @type {Validator<UnionToIntersection<Validated<V[number]>>>} */
  return (value) => validators.some((validator) => validator(value));
}

/**
 * @template {Record<string, unknown>} T
 * @param {ValidatorShape<T>} shape
 */
export function isShape(shape) {
  /** @type {Validator<T>} */
  return function (value) {
    if (typeof value !== "object" || value === null) return false;

    for (const key in shape) {
      if (!shape[key](Reflect.get(value, key))) return false;
    }

    return true;
  };
}

/**
 * @template T
 * @param {Validator<T>} validator
 */
export function isList(validator) {
  /** @type Validator<T[]> */
  return (value) => Array.isArray(value) && value.every(validator);
}

/**
 * @template {string | number | boolean | bigint | symbol | null | undefined} T
 * @param {T} literal
 */
export function isLiteral(literal) {
  /** @type Validator<T> */
  return (value) => value === literal;
}

/**
 * @template {"string" | "number" | "bigint" | "boolean" | "symbol" | "undefined" | "object" | "function"} T
 * @param {T} type
 */
export function isType(type) {
  /** @type {Validator<GetType<T>>} */
  return (value) => typeof value === type;
}

/**
 * @template {abstract new (...args: any[]) => any} T
 * @param {T} Constructor
 */
export function isInstance(Constructor) {
  /** @type {Validator<InstanceType<T>>} */
  return (value) => value instanceof Constructor;
}
