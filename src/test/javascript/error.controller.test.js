describe("ErrorJsController", function() {
    "use strict";

    beforeEach(module("casdemoApp"));

    let app;
    let scope;
    let controller;
    let window;

    beforeEach(inject(function($controller, $rootScope, $window, App, dataProvider) {
        scope = $rootScope.$new();
        window = $window;
        app = App;
        controller = $controller("ErrorJsController", {
            $scope: scope,
            $window: window,
            App: app,
            dataProvider: dataProvider
        });
    }));

    it("checkSubmit", function() {
        expect(controller).toBeDefined();

        let data = "";
        spyOn(scope, "submit").and.callFake(function() {
            data = "Slouches towards Bethlehem";
        });

        // What we are testing:
        scope.submit();

        expect(scope.submit).toHaveBeenCalled();
        expect(data).toEqual("Slouches towards Bethlehem");
    });

});
