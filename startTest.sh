mvn -Dbrowser="firefox" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site  test
mvn -Dbrowser="chrome" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test
mvn -Dbrowser="ie" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test
mvn -Dbrowser="phantomjs" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test

pause