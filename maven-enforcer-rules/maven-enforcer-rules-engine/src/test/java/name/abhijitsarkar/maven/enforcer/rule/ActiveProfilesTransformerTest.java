package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static name.abhijitsarkar.maven.enforcer.rule.TestUtil.createProfile;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Before;
import org.junit.Test;

public class ActiveProfilesTransformerTest {
    private MavenProject project;
    private MavenProject parentProject;

    @Before
    public void before() throws ExpressionEvaluationException {
	project = mock(MavenProject.class);

	List<Profile> profiles = asList(createProfile("a"), createProfile("b"));
	when(project.getActiveProfiles()).thenReturn(profiles);
    }

    @Test
    public void testTransformWhenNoParent() {
	Collection<String> profiles = new ActiveProfilesTransformer()
		.transform(project);

	for (String profile : new String[] { "a", "b" }) {
	    assertTrue(profiles.contains(profile));
	}
    }

    @Test
    public void testTransformWhenAParent() {
	parentProject = mock(MavenProject.class);
	when(project.getParent()).thenReturn(parentProject);

	List<Profile> profiles = asList(createProfile("c"), createProfile("d"));
	when(parentProject.getActiveProfiles()).thenReturn(profiles);

	Collection<String> p = new ActiveProfilesTransformer()
		.transform(project);

	for (String profile : new String[] { "a", "b", "c", "d" }) {
	    assertTrue(p.contains(profile));
	}
    }
}
