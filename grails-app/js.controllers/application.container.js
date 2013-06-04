var ApplicationContainer = {
    beans: {
        prototype: {},
        singleton: {}
    },

    register: function(name, ref, options) {
        if (!this.lookup(name)) {
            throw Error("A bean with name '" + name + "' has already been registered!");
        }

        switch (options.scope) {
            case "prototype":
                this.beans.prototype[name] = ref;
                break;
            default:
                if (options.lazy === false) {
                    this.beans.singleton[name] = ref;
                } else {
                    print("making new singleton");
                    this.beans.singleton[name] = new ref();
                }
        }
    },

    lookup: function(name) {
        var obj;
        if (this.beans.prototype.hasOwnProperty(name)) {
            obj = { scope: 'prototype', ref: this.beans.prototype[name] };
        } else {
            obj = { scope: 'singleton', ref: this.beans.prototype[name] };
        }

        return obj;
    },

    getBean: function(name) {
        var obj = this.lookup(name);
        if (!obj) throw Error("No bean by name '" + name + "' found!");

        var instance;
        switch (obj.scope) {
            case 'prototype':
                instance = new obj.ref();
                break;
            default:
                instance = obj.ref;
        }

        return instance;
    }
};

var Dispatcher = {
    SUFFIX: ".controller",

    dispatch: function(name, action, params) {
        var controller = ApplicationContainer.getBean(name + this.SUFFIX);
        return controller[action](params);
    },

    isHandled: function(name) {
        return !! ApplicationContainer.getBean(name + this.SUFFIX);
    }
};