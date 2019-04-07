### distributed-bellman-ford

time complexity: O(N)   
message complexity: O(NE)

#### input.dat

row1 -> number of nodes   
row2 -> root id   
remaining rows indicate weight matrix

#### compile and run

mvn clean package   
java -jar target/{sync.jar} <path_to_input_file> <path_to_output_log_file>
