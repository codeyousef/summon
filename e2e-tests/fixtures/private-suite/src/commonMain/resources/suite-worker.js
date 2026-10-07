const pending = new Map();

self.onmessage = event => {
  const message = event.data;
  if (!message || typeof message.id !== 'string') return;
  if (message.kind === 'cancel') {
    const timer = pending.get(message.id);
    clearTimeout(timer);
    pending.delete(message.id);
    return;
  }
  if (message.kind !== 'request' || !(message.payload instanceof Uint8Array)) return;
  const delay = message.payload[0] === 255 ? 750 : 0;
  const timer = setTimeout(() => {
    pending.delete(message.id);
    const payload = new Uint8Array(message.payload.length);
    for (let index = 0; index < message.payload.length; index++) {
      payload[index] = message.payload[message.payload.length - index - 1];
    }
    self.postMessage({ kind: 'response', id: message.id, payload });
  }, delay);
  pending.set(message.id, timer);
};
