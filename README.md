# RippleAssessment
This program is developed as a technical assessment for the Staff Integration Engineer role with Ripple.
The program makes HTTP requests to poll the rippled's `server_info` API using a JSON-RPC method. 
The response is parsed to extract the time and validated_ledger field values and writes them to a file. 

## Architecture
* [Server Connector]
* [File Writer]

## Usage
Clone the p

```shell
$ mvn -Dskip.tests=true compile
```
