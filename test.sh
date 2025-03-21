#!/bin/bash
curl -v http://localhost:8090/api/flink/table/api 2>&1 | tee table_api_response.txt 