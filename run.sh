#!/usr/bin/env bash
	sbt 'run 9167 -Dlogger.resource=logback-test.xml -Dplay.http.router=testOnly.Routes'