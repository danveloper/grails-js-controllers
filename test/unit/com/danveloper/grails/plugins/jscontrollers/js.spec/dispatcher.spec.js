describe("Dispatcher", function() {

    it("dispatcher properly delegates controller action", function() {
        function TestController() {
            this.index = function() {
                return "foo";
            }
        }

        ApplicationContainer.register('test.controller', TestController, { scope: 'prototype' });
        var result = Dispatcher.dispatch('test', 'index');
        expect(result).toBe("foo");
    });

});