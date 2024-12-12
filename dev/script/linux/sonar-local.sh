export SONAR_TOKEN=$(cat ~/.sonar/sonar-global)

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=fibonacci-msg-relay \
  -Dsonar.projectName='fibonacci-msg-relay' \
  -Dsonar.host.url=http://localhost:9000
