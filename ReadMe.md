**Description:**
- key.txt holds the API key, the program looks for API key here
- in src folder
	- Opswat.java - contains all the API calls
	- OpswatTest.java - conatins all the JUnit tests
	- StatusFamily.java - maps status code with a status, unfortunately my java version doesnt come with OOTB familiy for this so had to implement it
	- Utility.java - contains utility functions like, generateHash.
- dependencies
	- Java8 doesn't have OOTB functions to handle JSON so had to add a jar for it
	- The make file commands internally use this dependencies to compile and run so the user doesn't have to worry about it
- malware_samples
	- contains malware samples that I used for testing
	- you can also use it to test if you'd like
- tests
	- I have added JUnit tests for each request, however, additional jar is needed to run on your local machine the results of the tests are shown below

**How to run instructions:**
- I have added a make file to easy out the compilation process
- run, git clone https://github.com/rohitrai1/OPSWAT.git
- replace the existing key in the key.txt with your metadefeander key

- run the command "make compile" to generate class files 
- run command "make run ARGS=YOUR_FILE_LOCATION"
	- for example; make run ARGS=malware_samples/Trojan.NSIS.Win32/Trojan.NSIS.Win32.zip
- run make clean if you want to clean the class files that were generated by make compile
- demonstrated the above steps in the screenshot below

**How to run demo:**
![Can't display screenshot](https://github.com/rohitrai1/OPSWAT/blob/master/screenshots/how_to_run.png "How to run?")


**Test Result:**
![Can't display screenshot](https://github.com/rohitrai1/OPSWAT/blob/master/screenshots/test_result.png "Test Coverage?")

**Test Coverage:**
![Can't display screenshot](https://github.com/rohitrai1/OPSWAT/blob/master/screenshots/test_coverage.png "Test Coverage?")




