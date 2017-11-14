javac porter.java
javac Main.java
java Main tc_train stopword-list train-class-list model.model
java Main tc_test stopword-list model.model test-list test-class-list