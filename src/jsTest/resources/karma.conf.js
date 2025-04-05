module.exports = function(config) {
  config.set({
    frameworks: ['mocha', 'browserify'],
    reporters: ['progress'],
    files: [
      'kotlin/code/yousef/summon/components/display/TextJsTest.js',
      'kotlin/code/yousef/summon/components/display/ImageJsTest.js',
      'kotlin/code/yousef/summon/components/input/ButtonJsTest.js'
    ],
    exclude: [
      'kotlin/code/yousef/summon/components/display/TextTest.js',
      'kotlin/code/yousef/summon/components/display/ImageTest.js',
      'kotlin/code/yousef/summon/components/input/ButtonTest.js'
    ],
    preprocessors: {
      '**/*.js': ['browserify']
    },
    browsers: ['ChromeHeadless'],
    singleRun: true,
    client: {
      mocha: {
        timeout: 10000
      }
    }
  });
}; 