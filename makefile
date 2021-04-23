compile:
	javac -d ./ -cp dependencies/json-20210307.jar:dependencies/junit-jupiter-api-5.0.0-M4.jar src/*.java
run:
	java -cp .:dependencies/json-20210307.jar:dependencies/junit-jupiterD-api-5.0.0-M4.jar Opswat $(ARGS)

clean:
	rm -rf *.class
