#!/bin/bash

# JSON object to pass to Lambda Function
#json={"\"name\"":"\"Fred\u0020Smith\",\"param1\"":1,\"param2\"":2,\"param3\"":3}
json={"\"name\"":"\"\",\"calcs\"":2500000,\"sleep\"":0,\"loops\"":25}

echo $json


#echo "Invoking Lambda function using API Gateway"
time output=`curl -s -H "Content-Type: application/json" -X POST -d  $json https://4k8a0ku5g5.execute-api.us-east-1.amazonaws.com/calcs_test`
#time output=`curl -s -H "Content-Type: application/json" -X POST -d  $json {INSERT API GATEWAY URL HERE}`

#echo ""
echo "CURL RESULT:"
echo $output
#echo ""
#echo ""
exit

echo "Invoking Lambda function using AWS CLI"
#time output=`aws lambda invoke --invocation-type RequestResponse --function-name calcs --region us-east-1 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
aws lambda invoke --invocation-type RequestResponse --function-name logtest --region us-east-1 --payload $json /dev/stdout 
echo ""
echo "AWS CLI RESULT:"
echo $output
echo ""







