#!/usr/bin/env bash
	sbt 'run 9167 -Dlogger.resource=logback-test.xml -Dapplication.router=testOnly.Routes'