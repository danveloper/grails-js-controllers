function Callable(fn) {
    this.fn = fn;

    this.call = function() {
        fn();
    }
}

(function (global) {
    var timer = new java.util.Timer();
    var counter = 1;
    var ids = {};

    global.setTimeout = function (fn, delay) {
        var id = counter++;
        var callable = new Callable(fn);
        ids[id] = com.danveloper.grails.plugins.jscontrollers.javascript.TimerTaskFactory.createTimerTask(_jsr223Engine, callable);
        timer.schedule(ids[id], delay);
        return id;
    };

    global.clearTimeout = function (id) {
        ids[id].cancel();
        timer.purge();
        delete ids[id];
    };

    global.setInterval = function (fn, delay) {
        var id = counter++;
        var callable = new Callable(fn);
        ids[id] = com.danveloper.grails.plugins.jscontrollers.javascript.TimerTaskFactory.createTimerTask(_jsr223Engine, callable);
        timer.schedule(ids[id], delay, delay);
        return id;
    };

    global.clearInterval = global.clearTimeout;
})(this);