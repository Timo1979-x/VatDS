copy config.xml target/config.xml
copy config.xml target/config.xml2
cd target
java -Djava.security.debug=none -Dby.gto.btoreport.avest.password="V!kt0RPele^!" -Dby.gto.btoreport.avest.url="https://185.32.226.170:4443/InvoicesWS/services/InvoicesPort?wsdl" -jar btoReport-0.1.21.jar

pause
