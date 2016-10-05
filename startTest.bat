call mvn -Dbrowser="firefox" surefire-report:report -DreportsDirectory=${project.build.outputDirectory}${file.separator}target${file.separator}site  test 
call mvn -Dbrowser="chrome" surefire-report:report -DreportsDirectory=${project.build.outputDirectory}${file.separator}target${file.separator}site${file.separator}folder test
call mvn -Dbrowser="ie" surefire-report:report -DreportsDirectory=${project.build.outputDirectory}${file.separator}target${file.separator}site${file.separator}folder test
call mvn -Dbrowser="phantomjs" surefire-report:report -DreportsDirectory=${project.build.outputDirectory}${file.separator}target${file.separator}site${file.separator}folder test

pause