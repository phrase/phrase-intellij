# Plugin Development

## Development environment

1. Use the "Open" mechanism from the project chooser to add the project to your IDEA project rooster.
2. Add a debug or run configuration for the plugin, with the "Use alternative JRE" flag set to Android Studio.


## Dependencies

The Plugin depends on ApacheCommons, Unirest and JSON.org (aka "Jackson"). Jars must be provided with the Plugin .zip itself. To avoid conflicting versions of the libs above, create a Unirest-with-dependencies.jar as described in this guide: http://blog.mashape.com/installing-unirest-java-with-the-maven-assembly-plugin/
