package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static name.abhijitsarkar.maven.enforcer.rule.TestUtil.createProfile;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Before;
import org.junit.Test;

public class BannedProfilesTest {
    private MavenProject project;
    private EnforcerRuleHelper helper;
    private Log logger;

    @Before
    public void before() throws ExpressionEvaluationException {
	project = mock(MavenProject.class);
	helper = mock(EnforcerRuleHelper.class);
	logger = mock(Log.class);

	when(helper.evaluate("${project}")).thenReturn(project);
	when(helper.getLog()).thenReturn(logger);
    }

    @Test(expected = EnforcerRuleException.class)
    public void testWhenBannedProfileActive() throws EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a"), createProfile("b"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	BannedProfiles rule = new BannedProfiles();

	rule.setProfiles("a, b");
	rule.execute(helper);
    }

    @Test
    public void testWhenBannedProfileNotActive() throws EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a"), createProfile("c"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	BannedProfiles rule = new BannedProfiles();

	rule.setProfiles("b, d");
	rule.execute(helper);
    }

    @Test(expected = EnforcerRuleException.class)
    public void testWhenBannedRegexProfileActive() throws EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a1"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	BannedProfiles rule = new BannedProfiles();

	rule.setProfiles("a*");
	rule.execute(helper);
    }
}
