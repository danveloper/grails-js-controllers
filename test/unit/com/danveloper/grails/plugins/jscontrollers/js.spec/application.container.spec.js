describe("Application Container", function() {

    afterEach(function() {
        ApplicationContainer.beans.prototype = {};
        ApplicationContainer.registry = {};
    });

    it("controller registers properly", function() {
        function TestController() {}
        ApplicationContainer.register("test.controller", TestController, { scope: 'prototype' });
        var controller = ApplicationContainer.lookup("test.controller");
        expect(controller).not.toBe(null);
    });

    it("controller lookup resolves", function() {
        function TestController() {}
        ApplicationContainer.register("test.controller", TestController, { scope: 'prototype' });
        var controller = ApplicationContainer.getBean("test.controller");
        expect(controller).not.toBe(null);
    });

    it("controller inits properly", function() {
        var called = false;
        function TestController() {
            this.init = function() {
                called = true;
            }
        }

        ApplicationContainer.register("test.controller", TestController, { scope: 'prototype', init: 'init' });
        var controller = ApplicationContainer.getBean("test.controller");
        expect(called).toBe(true);
    });

});