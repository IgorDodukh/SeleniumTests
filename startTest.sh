mvn -Dbrowser="firefox" surefire-report:report -DreportsDirectory=${basedir}${file.separator}target${file.separator}site  test
mvn -Dbrowser="chrome" surefire-report:report -DreportsDirectory=${basedir}${file.separator}target${file.separator}site test
mvn -Dbrowser="ie" surefire-report:report -DreportsDirectory=${basedir}${file.separator}target${file.separator}site test
mvn -Dbrowser="phantomjs" surefire-report:report -DreportsDirectory=${basedir}${file.separator}target${file.separator}site test

pause