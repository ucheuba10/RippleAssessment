# RippleAssessment
This program is developed as a technical assessment for the Staff Integration Engineer role with Ripple.
The program makes HTTP requests to poll the rippled's `server_info` API using a JSON-RPC method. 
The response is then parsed to extract the `time` and `validated_ledger.seq` field values and writes them to a file.

## Architecture
* [Server Connector](#available-examples)
* [File Writer]

## Prerequisite
#### Download
Download or clone this repository

#### Configure
The program uses a properties file located at `src/main/resources/application.properties` for configuration. 
At the minimum, the following need to be configured:
* `server.url` - The http URL for the rippled server
* `file.output_file` - The absolute path to the output file that extracted response elements will be written to

#### Build
Use Maven to build the project with relevant dependencies

```shell
$ mvn -Dskip.tests=true compile
```
