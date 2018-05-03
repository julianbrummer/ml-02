To run just use the prebuild jar. The second parameter is the relative path to the arff file.
e.g.
java -jar ml-02-1.0.0.jar ./car.arff

To compile/deploy the application yourself you need to have maven installed.
In the directory, containing pom.xml call:
mvn install

The application uses a maven plugin called "lombok", which is used to generate some methods via annotations.
This library should be downloaded and deployed to your maven repository automatically.
However if you want to compile from an IDE such as Eclipse you may need to enable lombok manually by running
lombok.jar. See also:
http://www.vogella.com/tutorials/Lombok/article.html#lombok-eclipse