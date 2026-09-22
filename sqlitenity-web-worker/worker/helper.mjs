// @ts-check

export default function createWorker() {
  return new Worker(new URL("./worker.mjs", import.meta.url));
}
