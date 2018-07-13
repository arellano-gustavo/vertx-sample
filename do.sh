wget http://localhost:7777/app.js
mv app.js src/main/resources/assets
mvn clean package
java -jar target/crypto-executor-1.0-SNAPSHOT-fat.jar

