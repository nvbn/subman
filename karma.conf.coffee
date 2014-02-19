module.exports = (config) ->
  config.set
    basePath: '.'
    autoWatch: true
    reporters: ['progress']
    frameworks: ['jasmine']
    files: ['target/karma-test.js']
