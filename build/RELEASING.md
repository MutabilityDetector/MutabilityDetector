# Releasing

Mutability Detector is released to Maven Central via Sonatype's OSS repository. Official documentation [here](https://central.sonatype.org/publish/publish-maven/).

These notes were taken for the release of 0.10.6, Jan 2022. Things may have changed.

## ~/.m2/settings.xml

Ensure relevant settings are configured in `~/.m2/settings.xml`, including:
 * `ossrh` server section. Referenced in pom.xml for publishing snapshots.
 * GPG details for the `release` profile. For signing artifacts.

Example file:
```
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>the-username</username>
            <password>the-password</password> <!-- creds for the Nexus OSS repo -->
        </server>
    </servers>
    <profiles>
        <profile>
            <id>release</id>
            <activation>
                <activeByDefault>false</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>the-gpg-password</gpg.passphrase>
                <gpg.keyname>the-gpg-key-id</gpg.keyname> <!-- from gpg --list-keys, grundlefleck@gmail.com keypair -->
            </properties>
        </profile>
    </profiles>
</settings>

```

## Java

Build on Jdk 8: `sudo update-java-alternatives -s java-1.8.0-openjdk-amd64`


## Maven
```
# release plugin is used to tag the git repository and update the pom
mvn -P release release:prepare release:perform

# using the release profile to deploy will upload to Nexus OSS repositories
# when autoReleaseAfterClose=true in the config it will automatically go to Maven Central (eventually)
mvn -P release clean deploy
```

## Nexus
In Nexus terms "Close" is more like "Finalize". Performed before syncing to Maven Central, it performs verification on the uploaded distribution. The verification is prone to failure, particularly around GPG signing of artifacts. The `deploy` command will not fail due to verification, which happens asynchronously.

To tell whether a deploy will result in a release or not:
1. log in to Nexus repository manager [here](https://oss.sonatype.org/)
2. select `Staging Repositories`
3. find the release  
3.1 if nothing is there, it may have already passed verification, and be syncing with Maven Central. Sadly there's no recent history of the verification check to confirm  
3.2 if a repository is there with a red check, it's probably failed. Look at the "Activity" tab to find details and investigate  
