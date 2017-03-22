# Start application

    cd ./Integratie
    mvn compile exec:java -Dexec.mainClass=org.groepc.App

Open Chrome and go to:

    http://localhost:8080/api/notify

# Examples
https://github.com/christian-posta/fabric8-camel-rest-dsl/tree/master/src/main/java/org/devnation/demo/microservices_demo
http://blog.christianposta.com/camel/easy-rest-endpoints-with-apache-camel-2-14/

# Rain prediction per coordinate
http://gpsgadget.buienradar.nl/data/raintext/?lat=51.62&lon=4.88

# Tail the log file

    cd ./Integratie
    tail -f mylogging.txt