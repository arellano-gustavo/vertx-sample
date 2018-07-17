# wget http://localhost:7777/app.js
# mv app.js src/main/resources/assets
# mvn clean package
# java -jar target/crypto-executor-1.0-SNAPSHOT-fat.jar


# docker run -d -p 6160:6060 \
# -v /home/gustavo/kebblar-capital/executor:/engine \
# -v /home/gustavo/kebblar-capital/executor/log:/log \
# -v /home/gustavo/kebblar-capital/private:/private \
# -e CRED_FILE=/private/accounts.json \
# -e USER_ID=trader01 \
# gustavoarellano/jdk18 java -jar /engine/crypto-executor-1.0-SNAPSHOT-fat.jar


mvn package
scp target/crypto-executor-1.0-SNAPSHOT-fat.jar gustavo@192.168.100.14:/home/gustavo/kebblar-capital/executor
echo "ssh  gustavo@192.168.100.14"
