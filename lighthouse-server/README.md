# Lighthouse server for AET collector
Simple NodeJS server that exposes web API endpoint (`/api/v1/inspect`) for running Lighthouse
that will produce `json` report and return it in the response.

## Running
### Prerequisites
- [Install Lighthouse CLI](https://developers.google.com/web/tools/lighthouse/#cli) 

Run form the folder:
- `npm install` to install dependencies
- `npm run start` to start server at `5000` port

## Endpoint

### Request parameters
- `url` - url of the web page that will be tested with Lighthouse, e.g.
`/api/v1/inspect?url=http://github.com`

### Response
The `/api/v1/inspect` response is JSON that contains:
- `duration` - total time in ms of the Lighthouse execution
- `report` - the JSON result of the Lighthouse execution