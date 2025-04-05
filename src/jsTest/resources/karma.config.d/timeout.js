config.client = config.client || {};
config.client.mocha = config.client.mocha || {};
config.client.mocha.timeout = 60000;

// Increase timeouts for Karma itself
config.browserDisconnectTimeout = 30000;
config.browserNoActivityTimeout = 60000; 