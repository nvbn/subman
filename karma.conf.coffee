module.exports = (config) ->
  config.set
    basePath: '.'
    autoWatch: true
    reporters: ['progress']
    frameworks: ['jasmine']
    files: ["resources/public/components/jquery/dist/jquery.js"
            "resources/public/components/bootstrap/dist/js/bootstrap.js"
            "target/karma-test.js"]
