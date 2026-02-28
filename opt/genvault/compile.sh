./gradlew shadowJar

cp app/build/libs/app-all.jar .
mv app-all.jar genvault.jar
