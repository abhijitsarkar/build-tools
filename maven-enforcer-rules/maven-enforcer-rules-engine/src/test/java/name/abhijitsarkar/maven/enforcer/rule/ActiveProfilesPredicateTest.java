package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static name.abhijitsarkar.maven.enforcer.rule.TestUtil.createProfile;
import static org.apache.commons.collections4.CollectionUtils.exists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Before;
import org.junit.Test;

public class ActiveProfilesPredicateTest {
    private MavenProject project;
    private MavenProject parentProject;

    @Before
    public void before() throws ExpressionEvaluationException {
	project = mock(MavenProject.class);

	List<Profile> profiles = asList(createProfile("a"), createProfile("b"));
	when(project.getActiveProfiles()).thenReturn(profiles);
    }

    @Test
    public void testActiveProfilesWhenNoParent() {
	ActiveProfilePredicate predicate = new ActiveProfilePredicate(project);

	for (String profile : new String[] { "a", "b" }) {
	    assertTrue(exists(predicate.getActiveProfiles(),
		    new ConstantIdEqualPredicate(profile)));
	}
    }

    @Test
    public void testActiveProfilesWhenSomeParent() {
	parentProject = mock(MavenProject.class);
	when(project.getParent()).thenReturn(parentProject);

	List<Profile> profiles = asList(createProfile("c"), createProfile("d"));
	when(parentProject.getActiveProfiles()).thenReturn(profiles);

	ActiveProfilePredicate predicate = new ActiveProfilePredicate(project);

	assertNotNull(predicate.getActiveProfiles());
	assertEquals(4, predicate.getActiveProfiles().size());

	for (String profile : new String[] { "a", "b", "c", "d" }) {
	    assertTrue(exists(predicate.getActiveProfiles(),
		    new ConstantIdEqualPredicate(profile)));
	}
    }

    @Test
    public void testEvaluateWhenNoParent() {
	ActiveProfilePredicate predicate = new ActiveProfilePredicate(project);

	assertTrue(predicate.evaluate("a"));
    }

    @Test
    public void testEvaluateWhenSomeParent() {
	parentProject = mock(MavenProject.class);
	when(project.getParent()).thenReturn(parentProject);

	List<Profile> profiles = asList(createProfile("c"), createProfile("d"));
	when(parentProject.getActiveProfiles()).thenReturn(profiles);

	ActiveProfilePredicate predicate = new ActiveProfilePredicate(project);

	assertTrue(predicate.evaluate("a"));
	assertTrue(predicate.evaluate("d"));
    }

    static class ConstantIdEqualPredicate implements Predicate<Profile> {
	private final String profileName;

	public ConstantIdEqualPredicate(String profileName) {
	    this.profileName = profileName;
	}

	@Override
	public boolean evaluate(Profile profile) {
	    return profile.getId().equals(profileName);
	}
    }
}
