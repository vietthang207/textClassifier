# textClassifier

# Usage:
Compile : javac porter.java && javac Main.java

Copy the list files to the same directory with the Main class in order to train

Command to train: java Main tc_train <stopword_list> <train_class_list> <model_filename>

For example: java Main tc_train stopword-list train-class-list model.model

Command to run test file: java Main tc_test <stopword_list> <model_filename> <test_list> <test_class_list>

Note that <test_list> is not used.

For example: java Main tc_test stopword-list model.model test-list test-class-list

A script run.sh is provided for training and testing on the same training file

sh run.sh
