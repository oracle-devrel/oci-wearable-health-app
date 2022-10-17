# Wearable device admin UI demo App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Configure API endpoint for Development or Deployment

The API endpoint is configurable with environmental variable `REACT_APP_API_GATEWAY_ENDPOINT`:

- create-react-app will pickup environment variable `REACT_APP_API_GATEWAY_ENDPOINT` and expose to `process.env.REACT_APP_API_GATEWAY_ENDPOINT` in JavaScript. And UI will read this value and create a complete API endpoint URL with template `https://{process.env.REACT_APP_API_GATEWAY_ENDPOINT}/admin-api`
  - This value should be just domain name, without schema or any path
  - Example: `oracle.api.com`. In this case the actual API endpoint UI will hit is `https://oracle.api.com/admin-api`
- How to configure API endpoint for:
  - **Development**:
    - Create a file with name `.env` in the project folder at top level, in the file, set up API endpoint domain name like this: `REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com`
  - **Deployment**:
    - Set environment variable for different OS:
      - MacOS/Linux: in shell with commands like this `REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com` or `export REACT_APP_API_GATEWAY_ENDPOINT = oracle.api.com`
      - Windows: (in cmd.exe) `set "REACT_APP_NOT_SECRET_CODE=abcdef" && npm start`
 - Reference: https://create-react-app.dev/docs/adding-custom-environment-variables/

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
