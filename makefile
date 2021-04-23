compile:
	javac -d ./ -cp dependencies/json-20210307.jar:dependencies/junit-jupiter-api-5.0.0-M4.jar:dependencies:javax.xml.bind.jar src/*.java
run:
	java -cp .:dependencies/json-20210307.jar:dependencies/junit-jupiterD-api-5.0.0-M4.jar:dependencies:javax.xml.bind.jar Opswat $(ARGS)

run_tests:
	java -cp .:dependencies/json-20210307.jar:dependencies/junit-jupiterD-api-5.0.0-M4.jar  OpswatTest	
clean:
	rm -rf *.class
