describe("HolidayJsController", function() {
    "use strict";

    beforeEach(module("casdemoApp"));

    let app;
    let scope;
    let controller;

    beforeEach(inject(function($controller, $rootScope, App, dataProvider) {
        scope = $rootScope.$new();
        app = App;
        controller = $controller("HolidayJsController", {
            $scope: scope,
            App: app,
            dataProvider: dataProvider
        });
    }));

    it("basics", function() {
        expect(controller).toBeDefined();
        expect(controller.getDataProvider()).toBeDefined();
    });

    it("checkInitFunction", function() {
        spyOn(scope, "loadData").and.callFake(function() {
            scope.years.push(2009);
            scope.years.push(2013);
            scope.years.push(2011);
        });

        expect(scope.years).toBeDefined();
        expect(scope.years.length).toEqual(0);
        expect(app.Url.Api.HOLIDAY).toBeDefined();

        // What we are testing:
        scope.init();

        expect(scope.loadData).toHaveBeenCalled();

        expect(scope.years).toBeDefined();
        expect(scope.years.length).toEqual(4);

        expect(scope.years[0]).toEqual(new Date().getFullYear());
        expect(scope.years[1]).toEqual(2013);
        expect(scope.years[2]).toEqual(2011);
        expect(scope.years[3]).toEqual(2009);
    });

});
