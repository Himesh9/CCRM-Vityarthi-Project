CCRM Starter Project v0.2
====================

This is an extended starter Java project that implements:
 - GPA calculation & transcript printing
 - Grade entry
 - Import/Export (JSON & CSV) and Backup using NIO.2
 - Validators and custom exceptions
 - JUnit 5 tests and sample dataset (resources/sample_data.json)

Build & run (requires Maven and JDK 17+):
  mvn -q -DskipTests=false test
  mvn package
  java -cp target/ccrm-starter-0.2.0.jar edu.ccrm.Main

Files of interest:
 - src/main/java/... (domain, service, util, exception)
 - src/main/resources/sample_data.json

Notes:
 - The project uses org.json for lightweight JSON handling.
 - The tests operate on the singleton DataStore and thus are not isolated; they are illustrative smoke tests.
