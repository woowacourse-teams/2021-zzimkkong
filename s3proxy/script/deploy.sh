PROFILE=$1

JAR_FILE_NAME=s3proxy-0.0.1-SNAPSHOT.jar

echo "Checking currently running process id..."
RUNNING_PROCESS_ID=$(pgrep -fl s3proxy | awk '{print $1}')

if [ -z "$RUNNING_PROCESS_ID" ]; then
    echo "No s3Proxy server is running."
else
    echo "Killing process whose id is $RUNNING_PROCESS_ID"
    kill -15 $RUNNING_PROCESS_ID
    sleep 5
fi

echo "Running jar file..."
nohup java -jar $JAR_FILE_NAME -Dspring.profiles.active=$PROFILE > ~/nohup.out 2>&1 &
