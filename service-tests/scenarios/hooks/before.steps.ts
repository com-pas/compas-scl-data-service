import { Before } from "@cucumber/cucumber";
import { TestWorld } from "@ase-compas/compas-service-tests/support/world";

Before(async function (this: TestWorld, scenario) {
    this.log(`Starting: ${scenario.pickle.name}`);
    this.requestHeaders = {};
    this.savedValues = {};
    this.savedValues.cleanUpTasks = [];
});
