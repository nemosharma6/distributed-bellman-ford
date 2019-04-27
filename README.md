### distributed-bellman-ford

time complexity: O(N)   
message complexity: O(NE)

#### input.dat

row1 -> number of nodes   
row2 -> root id   
remaining rows indicate weight matrix

#### internal parameter
MaxRoundGap -> parameter to simulate async message transfer.

#### compile and run

mvn clean package   
java -jar target/{async.jar} <path_to_input_file> <path_to_output_log_file>
