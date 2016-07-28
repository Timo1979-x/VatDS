rem запускать лучше из папки, где нет файла pom.xml

rem mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=path-to-your-artifact-jar -DgroupId=your.groupId -DartifactId=your-artifactId -Dversion=version -Dpackaging=jar -DlocalRepositoryPath=path-to-specific-local-re