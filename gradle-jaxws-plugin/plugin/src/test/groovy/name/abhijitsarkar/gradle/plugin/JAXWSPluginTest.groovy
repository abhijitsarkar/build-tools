package name.abhijitsarkar.gradle.plugin

import name.abhijitsarkar.gradle.plugin.JAXWSPlugin;
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class JAXWSPluginTest {
	private Project project

	@Before
	public void setUp() {
		ProjectBuilder projectBuilder = ProjectBuilder.builder()
		projectBuilder.withName("jaxws-plugin")
		projectBuilder.withProjectDir(new File("."))

		project = projectBuilder.build()

		project.apply plugin: "groovy"
		project.apply plugin: JAXWSPlugin
	}

	@Test
	public void testWsimportPluginAdded() {
		assert project.plugins.hasPlugin(JAXWSPlugin.class)
	}

	@Test
	public void testWsimportTaskAdded() {
		assert project.tasks.findByName("wsimport") != null
	}
}