# Plugin Development

## Development enviroment

1. Install JDK6 for OSX: https://support.apple.com/kb/DL1572
2. Install JDK7/8 
3. Setup a working IntelliJ Plugin development enviroment as described in this guide: http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes - use JDK6 for Plugin bytecode

## Dependenceis

The Plugin depends on ApacheCommons, Unirest and JSON.org (aka "Jackson"). Jars must be provided with the Plugin .zip itself. To avoid conflicting versions of the libs above, create a Unirest-with-dependencies.jar as described in this guide: http://blog.mashape.com/installing-unirest-java-with-the-maven-assembly-plugin/
