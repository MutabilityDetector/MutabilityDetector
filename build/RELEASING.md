# Releasing to Maven Central


Official documentation [here](https://central.sonatype.org/publish/publish-maven/).

Ensure `ossrh` server is set up in `~/.m2/settings.xml`
Build on Jdk 8: `sudo update-java-alternatives -s java-1.8.0-openjdk-amd64`


mvn -Drelease clean deploy
mvn -Drelease release:prepare release:perform

Once staged, to release:
1. log in to Nexus repository manager [here](https://oss.sonatype.org/)
2. select `Staging Repositories`
3. find the release
4. select "Close"
5. the Maven Central sync requirements will be evaluated, hit "Refresh" and look at the "Activity" tab for the release
6. if successful, hit "Release"
