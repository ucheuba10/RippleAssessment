# RippleAssessment
This program is developed as a technical assessment for the Staff Integration Engineer role with Ripple.
The program makes HTTP requests to poll the rippled's `server_info` API using a JSON-RPC method. 
The response is then parsed to extract the `time` and `validated_ledger.seq` field values and writes them to a file.


## Getting Started

#### Download
Download or clone this repository

#### Configuration
The program uses a properties file located at [`src/main/resources/application.properties`](src/main/resources/application.properties) for configuration. 
At the minimum, the following parameters need to be configured:
* `server.url` - The HTTP URL for the rippled server `server_info` API endpoint.
* `file.output_file` - The absolute path to the output file that extracted response elements will be written to.

Additional useful configurations include:
* `server.polling_interval_ms` - The interval in milliseconds to poll the server for data.
* `server.poll_count` - Control the number of times to poll the server every time the program is run.

#### Build
Use Maven to build the project. This will build with relevant dependencies.

```shell
$ mvn -Dskip.tests=true compile
```


## Components

<a name="http-connector"/>

### Http Connector:
The [`HttpClientConnector`](src/main/java/com/uche/rippled/HttpClientConnector.java) is a configurable HTTP client implementation which is used to send HTTP requests 
(using one of the standard HTTP methods: GET, POST, PUT) to the server and handles HTTP responses returned. 


<a name="file-connector"/>

### File Connector:
The [`SequenceFileConnector`](src/main/java/com/uche/rippleassessment/SequenceFileConnector.java) is used to append a string as a new line to the end of a file. 


<a name="orchestrator"/>

### Orchestrator:
The [`Orchestrator`](src/main/java/com/uche/rippleassessment/Orchestrator.java) manages the flow of the integration. At a high level, the steps taken are:
* Instantiating a [HTTP client](#http-connector) to connect with the rippled server. The client is created with a response, 
retry and keep-alive handler.
* Create a HTTP POST request for the `server_info` RPC call. Necessary headers and body are set for the request 
* Polls the rippled server for data, by sending the HTTP POST request at intervals over the HTTP client connection. 
(The interval for polling is configurable in the properties file with parameter `server.polling_interval_ms`. The value is in milliseconds.)
* Parse each response received, extract the fields `time` and `validated_ledger.seq` and format into a new delimited line
* Send the new line to the [File Connector](#file-connector) to be written to file