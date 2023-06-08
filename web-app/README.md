# Wearable device admin UI demo App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Configure Environment variables for Development / Deployment

1. The API endpoint is configurable with environmental variable `REACT_APP_API_GATEWAY_ENDPOINT`
2. The base URL (for accessing assets correctly) is configurable with environmental variable `PUBLIC_URL`
3. The UI tab/window title in browser is configurable with environmental variable `REACT_APP_WEBSITE_NAME`

- ## How to configure API endpoint through `REACT_APP_API_GATEWAY_ENDPOINT`
  
  - The API endpoint template for this UI is `https://{process.env.REACT_APP_API_GATEWAY_ENDPOINT}/admin-api` , the API endpoint can be configured by setting environment variable `REACT_APP_API_GATEWAY_ENDPOINT`.
    - Example: If the desired API endpoint is `https://oracle.api.com/admin-api`, then need to set `REACT_APP_API_GATEWAY_ENDPOINT` = `oracle.api.com`.
  - How to configure API endpoint for:
    - **Development**:
      - Create a file with name `.env` in the project folder at top level, in the file, set up API endpoint domain name like this: `REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com`
    - **Deployment**:
      - Set environment variable for different OS:
        - MacOS/Linux: in shell with commands like this `REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com` or `export REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com`
        - Windows: (in cmd.exe) `set "REACT_APP_API_GATEWAY_ENDPOINT=abcdef" && npm start`

    - How it works
      - create-react-app will pickup environment variable `REACT_APP_API_GATEWAY_ENDPOINT` and expose to `process.env.REACT_APP_API_GATEWAY_ENDPOINT` in JavaScript. And UI will read this value and create a complete API endpoint URL with template `https://{process.env.REACT_APP_API_GATEWAY_ENDPOINT}/admin-api`
      - This value should be just domain name, without schema or any path

- ## How to configure base URL through `PUBLIC_URL`

  - `PUBLIC_URL` is for specifying the public folder/path containing UI assets so they can be accessed correctly
  - One example use case: When assets is hosted in an object storage service, and there is no web server in between handling serving files (such as js/css) when accessing the UI. For web UI assets to be loaded correctly from index.html, it is necessary to set environment variable.
  
- ## Example command for building web UI bundle with environment variables configurations
  
  - `export PUBLIC_URL = https://objectstorage.us-ashburn-1.oraclecloud.com/n/{userName}/b/{bucketName}}/o/ && export REACT_APP_API_GATEWAY_ENDPOINT={apiGatewayId}.apigateway.us-ashburn-1.oci.customer-oci.com && export REACT_APP_WEBSITE_NAME="Wearable Device Demo" && yarn build`

- Reference: https://create-react-app.dev/docs/adding-custom-environment-variables/

## Authentication methods
  
- The admin UI has integration with two Authentication methods: 
  - `app-based` : In Log in UI, clicking on "Sign in" button is for app-based authentication.
    - User submit credentials to login API endpoint, and get a JWT token after successful authentication, web app will carry the token in **Authorization** header in following API calls for authorization.
  - `Okta` : In Log in UI, clicking on "Okta sign in" is for Okta based authentication, clicking on the button will open a popup child window with Okta login page.
    - The app has a SSO(Single Sign-On) integration with Okta (3rd party IAM service). OCI APIGateway is integrated with Okta, and will pass `X-Apigw-Token` and `token` back to web app. In the following authorization, `X-Apigw-Token` will be passed in API **request header**, and `token` will be passed in API **request param**.

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
