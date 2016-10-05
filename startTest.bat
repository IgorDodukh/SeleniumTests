call mvn -Dbrowser="firefox" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site  test
call mvn -Dbrowser="chrome" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test
call mvn -Dbrowser="ie" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test
call mvn -Dbrowser="phantomjs" surefire-report:report -DreportsPath=${basedir}${file.separator}target${file.separator}site test

pause