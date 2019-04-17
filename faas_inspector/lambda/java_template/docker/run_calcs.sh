#Large arrays, few calls:
perf stat -e dTLB-store-misses java -cp lambda_test-1.0-SNAPSHOT.jar lambda.Calcs 1000000 0 10

#
#Small arrays, many calls:
perf stat -e dTLB-store-misses java -cp lambda_test-1.0-SNAPSHOT.jar lambda.Calcs 1000 0 10000

