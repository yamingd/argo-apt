package com.argo.gradle
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import spock.lang.Specification

class AptPluginSpec extends Specification {

  def "it should generate sources for java project"() {
    def projectPath = 'src/test/build-tests/java-project'
    def aptBasePath = "$projectPath/build/source/apt"

    given:
    ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(new File(projectPath)).connect()

    when:
    try {
      connection.newBuild().forTasks("clean", "build").run()
    } finally {
      connection.close()
    }

    then:
    new File(aptBasePath).exists()
    new File("$aptBasePath/coffee").listFiles().length > 0
  }


}
