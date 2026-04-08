#!/usr/bin/env bash
set -euo pipefail

# Ensures sibling modules are installed so api-layer can be launched directly.
mvn -f "$(dirname "$0")/../pom.xml" -pl api-layer -am install
mvn -f "$(dirname "$0")/../pom.xml" -pl api-layer spring-boot:run
