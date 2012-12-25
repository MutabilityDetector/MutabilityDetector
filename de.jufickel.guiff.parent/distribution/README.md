# guiff: Version ${project.version}
This directory contains the distribution of guiff, a tool for
extracting the audio stream of a video file into the same directory.


## Requirements
guiff will run with any version of Java equal or greater than 1.6.
Building from source requires [Apache Maven](http://maven.apache.org/)
version 3.0.4 or greater.
Also it is necessary that ffmpeg is on the path of the operating system.


## Development

### Build
To build guiff from the sources simply type 

    mvn clean package

on command line (parent module). This creates the binary and source
distribution. Both is being placed in the `target` directory of module
`distribution`.


### Javadoc
The API documentation for the whole application can be built by typing

    mvn package javadoc:aggregate

on command line (parent module). It is necessary that the current working
directory is the one which contains the `pom.xml` of the parent module.
The result can be found in directory `target/site/apidocs` of the parent
module.


### License file header comments
guiff uses the
[`maven-licence-plugin`](https://code.google.com/p/maven-license-plugin/).
It is configurated in the parent module’s `pom.xml`. To add or update all header
comments, type

    mvn licence:format

on command line, while you are in the parent module’s directory.
To remove all licence header comments, type

    mvn licence:remove


### Import as Eclipse project
To work with this project in Eclipse, the plugin `m2e` is needed, which
integrates Maven into Eclipse. After the source distribution of guiff
was extracted, its contents can be imported as Eclipse projects. The way
to go is "File", "Import", "Maven", "Existing Maven Projects". Next choose
the extracted source folder as "Root Directory". The remaining steps are
explained by the assistant dialogue.


## Usage
There exists only an graphical user interface (GUI) of guiff.


### Swing GUI
To start guiff with its Swing GUI simply type

    ./guiff or ./guiff.exe

on the command line. After that a single window appears (wizard) on the screen
to assist you with creating to achieve the rest.


## Licencing
This software is licensed under the terms in the file named "LICENCE.txt" in
this directory.

This product includes software developed by
[The Apache Software Foundation](http://www.apache.org/).
