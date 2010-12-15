package searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.apache.maven.artifact.Artifact;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import searls.jasmine.io.FileUtilsWrapper;
import searls.jasmine.io.IOUtilsWrapper;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorIntegrationTest {

	private String excludes = "exclude";
	private String includes = "include";
	
	private SpecRunnerHtmlGenerator specRunnerHtmlGenerator;
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private File sourceDir;
	@Mock private File destDir; 
	@Spy private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	
	@Before 
	public void setUp() {
		 specRunnerHtmlGenerator = new SpecRunnerHtmlGenerator(null, sourceDir,
				 includes, excludes, destDir, fileUtilsWrapper);
	}

	@Test
	public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
		List<Artifact> deps = new ArrayList<Artifact>();
		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		assertThat(html, containsString("<html>"));
		assertThat(html, containsString("</html>"));
	}

	@Test
	public void shouldPutInADocTypeWhenNoDependenciesAreProvided() {
		List<Artifact> deps = new ArrayList<Artifact>();
		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		assertThat(html, containsString("<!DOCTYPE html>"));
	}

	@Test
	public void shouldPopulateJasmineSourceIntoHtmlWhenProvided() throws Exception {
		String expectedContents = "javascript()";
		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine", "1.0.1", "js", expectedContents));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<script type=\"text/javascript\">" + expectedContents + "</script>"));
	}

	@Test
	public void shouldPopulateMultipleJavascriptSourcesIntoHtmlWhenProvided() throws Exception {
		String jasmineString = "javascript_jasmine()";
		String jasmineHtmlString = "javascript_jasmine_html()";
		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine", "1.0.1", "js", jasmineString));
		deps.add(mockDependency("com.pivotallabs", "jasmine-html", "1.0.1", "js", jasmineHtmlString));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<script type=\"text/javascript\">" + jasmineString + "</script>"));
		assertThat(html, containsString("<script type=\"text/javascript\">" + jasmineHtmlString + "</script>"));
	}

	@Test
	public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
		String css = "h1 { background-color: awesome}";

		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine-css", "1.0.1", "css", css));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<style type=\"text/css\">" + css + "</style>"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotReadDefaultTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);
		
		specRunnerHtmlGenerator.generate(Collections.EMPTY_LIST, ReporterType.TrivialReporter, expected);

		verify(ioUtilsWrapper,never()).toString(isA(InputStream.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);
		
		specRunnerHtmlGenerator.generate(Collections.EMPTY_LIST, ReporterType.TrivialReporter, expected);

		verify(fileUtilsWrapper).readFileToString(expected);
	}
	
	@Test 
	public void handsIncludeAndExcludeToFileUtils() throws IOException {
		List<Artifact> deps = new ArrayList<Artifact>();
		specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		verify(fileUtilsWrapper).listFiles(sourceDir, includes, excludes);
		verify(fileUtilsWrapper).listFiles(destDir, includes, excludes);
	}
	
	private Artifact mockDependency(String groupId, String artifactId, String version, String type, String fileContents) throws Exception {
		Artifact dep = mock(Artifact.class);
		when(dep.getGroupId()).thenReturn(groupId);
		when(dep.getArtifactId()).thenReturn(artifactId);
		when(dep.getVersion()).thenReturn(version);
		when(dep.getType()).thenReturn(type);

		File f = mock(File.class);
		when(fileUtilsWrapper.readFileToString(f)).thenReturn(fileContents);
		when(dep.getFile()).thenReturn(f);

		return dep;
	}

}