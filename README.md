# oci-wearable-health-app

[![License: UPL](https://img.shields.io/badge/license-UPL-green)](https://img.shields.io/badge/license-UPL-green) [![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=oracle-devrel_oci-wearable-demo)](https://sonarcloud.io/dashboard?id=oracle-devrel_oci-wearable-demo)

## THIS IS A NEW, BLANK REPO THAT IS NOT READY FOR USE YET.  PLEASE CHECK BACK SOON!

## Introduction

The sample application is an IoT use-case, in which an application capturing health parameters running on a wearable device is sending health statistics to a device gateway (backend) hosted on OCI on regular intervals. 
The complete use-case can be split into 3 different parts:
* On-boarding and Administration: As the device is bought by an end-user, the device is on-boarded and activated on the system via a mobile app or a web app. The mobile / web-app calls REST endpoints that are front-ended by an API Gateway. The REST endpoints are hosted on serverless functions and are responsible for activating the device on the system, adding a user into system, mapping the end-user with the device, defining alert thresholds of a user, amongst other functionalities.
* Health data capturing & Real time Analytics - The data captured by the health devices is sent to a public end-point exposed via a network load balancer. The NLB forwards the request to device gateway, which transform the raw TCP data into more meaningful format like JSON and also enrich it with the user / contextual information. These TCP adapters are deployed over OKE. The transformed data is pushed into OCI Streaming service for further processing. There can be multiple consumers reading these events from the streaming service

  * Backup Service - writing data into a data lake for analytics, this is the transformed & enriched payload, which is kept for long term duration.
  * Dataflow job - Spark job doing near real-time analytics like any threshold breach. This jobs also pushes the payload & generated alerts into time-series DB (e.g. OCI's NoSQL DB service). This persisted data can be served by REST endpoints on an end-user application. The generated alerts are also pushed to a queue, which is consumed by the Notification Service (running on OKE), the notification service is responsible for sending the alerts to the intended user via different channels like SMS / Email.

* Batch Analytics - The data stored in the data lake bucket can be processed using OCI Big DataService, that can aggregate data over different dimensions and time durations. This aggregated data can then be rendered on dashboards for use by OEM, end-users or medical practitioners.

## Getting Started
MISSING

### Prerequisites
MISSING

## Notes/Issues
MISSING

## URLs
* Nothing at this time

## Contributing
This project is open source.  Please submit your contributions by forking this repository and submitting a pull request!  Oracle appreciates any contributions that are made by the open source community.

## License
Copyright (c) 2022 Oracle and/or its affiliates.

Licensed under the Universal Permissive License (UPL), Version 1.0.

See [LICENSE](LICENSE) for more details.

ORACLE AND ITS AFFILIATES DO NOT PROVIDE ANY WARRANTY WHATSOEVER, EXPRESS OR IMPLIED, FOR ANY SOFTWARE, MATERIAL OR CONTENT OF ANY KIND CONTAINED OR PRODUCED WITHIN THIS REPOSITORY, AND IN PARTICULAR SPECIFICALLY DISCLAIM ANY AND ALL IMPLIED WARRANTIES OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY, AND FITNESS FOR A PARTICULAR PURPOSE.  FURTHERMORE, ORACLE AND ITS AFFILIATES DO NOT REPRESENT THAT ANY CUSTOMARY SECURITY REVIEW HAS BEEN PERFORMED WITH RESPECT TO ANY SOFTWARE, MATERIAL OR CONTENT CONTAINED OR PRODUCED WITHIN THIS REPOSITORY. IN ADDITION, AND WITHOUT LIMITING THE FOREGOING, THIRD PARTIES MAY HAVE POSTED SOFTWARE, MATERIAL OR CONTENT TO THIS REPOSITORY WITHOUT ANY REVIEW. USE AT YOUR OWN RISK. 