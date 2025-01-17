# Ripple Tech Assessment
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
* Update the file path for csv data file in the `chart.gnuplot` script to reference the same output file.

Additional useful configurations include:
* `server.polling_interval_ms` - The interval in milliseconds to poll the server for data.
* `server.poll_count` - Control the number of times to poll the server every time the program is run.

#### Build and Run
Use Maven to build and run the project. This will build with relevant dependencies. 

> You can use the IDE of your choice
>
> The main class is com.uche.rippleassment.Orchestrator
>
> When program is running, it displays statistics to console

```shell
$ cd /path/to/RippleAssessment
$ mvn -Dfile.encoding=UTF-8 install install
$ mvn -Dexec.args="-classpath %classpath com.uche.rippleassessment.Orchestrator" -Dexec.executable=java.exe 
```

#### Plotting Chart
After running the java program, run the `chart.gnuplot` script from the gnuplot console to plot a chart with the generated data.
(This requires gnuplot to already be installed and in your class path)

```shell
$ gnuplot
gnuplot> load 'chart.gnuplot'
```



## Components

<a name="http-connector"/>

### Http Connector:
The [`HttpClientConnector`](src/main/java/com/uche/rippled/HttpClientConnector.java) is a configurable HTTP client implementation which is used to send HTTP requests 
(using one of the standard HTTP methods: GET, POST, PUT) to the server and handles HTTP responses returned. 


<a name="file-connector"/>

### File Connector:
The [`SequenceFileConnector`](src/main/java/com/uche/rippleassessment/SequenceFileConnector.java) is used to append a string as a new line to the end of a file. 


<a name="data-transformation"/>

### Data Transformation:
The [`RippledServer`](src/main/java/com/uche/rippled/RippledServer.java) handles data format for communicating with the server. 
* It creates the JSON payload for sending to the API
* Parses the JSON response and transforms to the delimited text for writing to file.


<a name="orchestrator"/>

### Orchestrator:
The [`Orchestrator`](src/main/java/com/uche/rippleassessment/Orchestrator.java) manages the flow of the integration. At a high level, the steps taken are:
* Instantiating a [HTTP client](#http-connector) to connect with the rippled server. The client is created with a response, 
retry and keep-alive handler.
* Create a HTTP POST request for the `server_info` RPC call. Necessary headers and body are set for the request 
* Polls the rippled server for data, by sending the HTTP POST request at intervals over the HTTP client connection. 
(The interval for polling is configurable in the properties file with parameter `server.polling_interval_ms`. The value is in milliseconds.)
* Parse each response received, extract the fields `time` and `validated_ledger.seq` and format into a new delimited line using the [Data Transformation](#data-transformation)
* Send the new line to the [File Connector](#file-connector) to be written to file
* In addition, the time and sequence information is run through an [algorithm method](src/main/java/com/uche/rippleassessment/CalculateStats.java) to calculate ledger validation time statistics (Min, Max, Average).
