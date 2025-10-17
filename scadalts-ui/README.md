# Scada-LTS - Vue User Interface

Vue.js subproject for modern Views and Components

## Description

User Interface subproject to display ScadaLTS content.  
Image assets from this project are copied into `WebContet/img` directory.  
Example usage of images was presented in `src/views/LoginPage/index.vue`

## Usage

- To start a developmnent server type inside this directory:  
  `npm run-script serve`
- To run unit test type:  
  `npm run-script test:unit`
- To run E2E cypress tests type:  
  `npm run-script test:e2e`
- To run Cypress testing framework type:  
  `npm run-script cypress`
- To build the fornt-end application type:  
  `npm run-script prettier && npm run-script build`  
   _This code runs prettier and then builds the application to dist directory_

## Using the developmnet server
When you start a development server to instant application building you have to log into the Scada-LTS. Dev server uses `"http-proxy-middleware-secure-cookies"` dependency to handle the connection within frontend and core application. When your server is ready you have to open your browser on the `http://localhost:3000/#/` and you should be redirected to login page. There you have to open the DevTools (F11 in Chrome) and open "Network" tab to monitor the HTTP requestes. Try to log into the application using the default password.
When you receive the `xhr response` with RequestURL `*/api/auth/...` from server open that request details and find the **Response Headers** section. Then find a "set-cookie" header and copy that value. Then paste that cookie inside your DevServer because you were propably prompted to input a auth cookie there. If everything is ok you should see the  
`"Successfully saved your cookie. Please refresh."` message from the WebServer. Then you can easily develop the frontend application with on-fly updates and access to backend data. 

If you have an issue with that try to logout from the core ScadaLTS application (that which is hosted on port 8080) and make sure cookies has been deleted there. In Chrome dev-tools open the "Appication" tab and click "Clear all cookies" button.

## Cypress Testing

Before starting the Cypress Tests make sure that you are using blank Database to ensure the TestingSuite the clear Scada-LTS application otherwise more tests may fail.

[Here](./cypress/README.md) you can find Tutorial presenting how to create Cypress Tests for Scada-LTS.

## Creators

Developers from Scada-LTS project:

- @grzesiekb
- @radek2s
