mvn -Dbrowser="firefox" surefire-report:report -DouputFile=YourFileName.html test 
mvn -Dbrowser="chrome" surefire-report:report -DouputFile=YourFileName.html test
mvn -Dbrowser="ie" surefire-report:report -DouputFile=YourFileName.html test
mvn -Dbrowser="phantomjs" surefire-report:report -DouputFile=YourFileName.html test

pause