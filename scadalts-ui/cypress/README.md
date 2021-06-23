# Scada-LTS - Cypress Testing

Cypress Test Runner is the tool to create and run Integration Tests. It provides many useful tools like integrated browser driver and do not require external webdriver to run tests.

## Directory structure

- **fixtures** -> Set of example configuration data that can be loaded to the Scada-LTS application
- **integration** -> Integration Tests files
- **plugins** -> additional plugins for test runner
- **screenshots** -> location of test run screenshoots
- **support** -> additional utils that extend the functionality of Cypress Runner.
- **utils** (deprecated) -> place with util functions.
- **videos** -> location to save Test Run recoding.

## Writting simple test

Most tests are written for Moder User Interface validation to improve the quality of the Scada-LTS application. Before releasing a new Scada-LTS version, all checks must be passed. This test runner require a running Scada-LTS core application with established database connection (but Database should be blank).

Test are divied into specific files with `*.spec.js` extension. We use **snake_case** file naming convention for that files. Tests that are related to View on the Modern User Interface are located inside `integration/ModernUI` directory. Tests that check the elements on the Classic User Interface are placed inside `integration/ScadaLTS_Views` folder. If you want just to check the single component try to keep the test files inside `integration/components` direcotry.

### Quick start guide

Single file can contain multiple testing scenarios.
Each scenario is divided into specific Test Suite that provide multiple test cases. For Example:

```
- simple_test.spec.js

  * Scenario - Point Details Page validation

    + Test - Validate visible elements
      - Is Header rendered properly
      - Is Data Point table rendered properly
      ...

    + Test - Save basic configuration
      - Create Point Event Detector
      - Change Text Renderer
      ...
    ...

  * Scenario - Data Point Properties validation
  (etc.)
  ...

- another_test_file.spec.js
```

Each test Scenario should leave the Bussines Object state as it was before test execution. To keep the aplication clean you should use `before(() => {})` and `after(() => {})` hooks that prepare and clear the test configuration.

To make the preparation easier there is an method that extends the cypress functionality. **cy.loadConfiguration()** method open Import/Export Scada-LTS page nad load JSON configuration into the application.

```JavaScript
// Load Scada-LTS configuration from fixture JSON file.
cy.fixture('<path-to-fixture-file>').then(config => {
    cy.loadConfiguration(config)
});
```

But!  
Most of the functionality (like sending or receiving requests) require user authorization. To handle that problem there was prepared another extension that use REST interface to authorize the user.

```JavaScript
// Authorize as an administrator user
cy.restLogin()

// Authorize as 'temp_user' with 'temp' password
cy.restLogin('temp_user', 'temp')
```

As you can see on the example you are able to login into the Scada-LTS application as other user but you must remember that before that you have to create that user inside Scada-LTS.

⚠️ See [Tutorial for writting your first Cypress Test](https://docs.cypress.io/guides/getting-started/writing-your-first-test.html#Add-a-test-file).

### Example test

Here you can find the example initial test structure:

```JavaScript
//Loading configuration
before(() => {
    cy.restLogin();
    cy.fixture('ExampleConfig').then(configuration => {
        cy.loadConfiguration(configuration);
    });
})

context("Scenario - Example Test Scenario", () => {

    describe("Test - Example Test Suite", () => {

        it('Example Test Case', () => {

            cy.get('h1').contains('Title'); //Example expectation
        });

        ...
    });

    ...
});

```

## Using CypressTest Runner

To run your tests you have to start the Cypress application. It can be easly done with this simple command typed inside your `/scadalts-ui` directory:

```console
npm run-script cypress
```

### Run headless tests

You can start also all test without opening the browser by just typing:

```console
npm run-script test:cypress
```
