var JasmineSpecRunner = {
    jasmineEnv: jasmine.getEnv(),
    reporter: new jasmine.JsApiReporter(),
    results: [],

    init: function() {
        var self = this;
        self.jasmineEnv.updateInterval = 1000;
        self.jasmineEnv.addReporter(this.reporter);
    },

    exec: function() {
        this.jasmineEnv.execute();
    },

    getResults: function() {
        return this.results;
    },

    isFinished: function() {
        return this.reporter.isFinished();
    }

};

JasmineSpecRunner.init();

// Extend the JsApiReporter so that we can see if it's done or not...
jasmine.JsApiReporter.prototype.isFinished = function() {
    return this.finished;
};

jasmine.JsApiReporter.prototype.reportSpecResults_ = jasmine.JsApiReporter.prototype.reportSpecResults;
jasmine.JsApiReporter.prototype.reportSpecResults = function(spec) {
    JasmineSpecRunner.results.push(spec.results_);
    return this.reportSpecResults_(spec);
};