# SocioSeer App

SocioSeer is a cloud-enabled application designed in microservice fashion using Spring Cloud stack, in order to setup project follow below steps:-
 1. Clone repository from [here](https://akhil-chaurasia@bitbucket.org/akhil-chaurasia/socio-seer-app.git) 
 2. Move to project directory and run ("mvn clean package -DskipDockerBuild")
 3. This project uses lombok library to remove boilerplate code such as  getter/setter/constructors etc , for installation in IDE please refer below:- 
**IntelliJ** :  if you are using IntelliJ then it come installed otherwise update your IntelliJ.
**Eclipse** : if you are using eclipse then we need to install it manually, for this download lombok jar (version -> 1.16.14) and run it  and by using below command:-
java -jar lombok-1.14.6.jar
After this it will prompt for Eclipse already installed in your system and you need to select where you want to integrate. 
After this you need to open eclipse.ini file and make entry below

    -vmargs

as

> -Xbootclasspath/a:lombok.jar
-javaagent:lombok.jar

 Re-Start your eclipse, if the getter/setter code is not auto generated then select clean project and see in outline.


