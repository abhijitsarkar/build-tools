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

public class RequireMutuallyExclusiveActiveProfilesTest {
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
    public void testWhenNotMutuallyExclusiveProfiles()
	    throws ExpressionEvaluationException, EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a"), createProfile("b"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	/* Throw in some random spaces and see if the rule can handle it. */
	rule.setProfiles("b, c: a,b");

	rule.execute(helper);
    }

    @Test
    public void testWhenMutuallyExclusiveProfiles()
	    throws ExpressionEvaluationException, EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a"), createProfile("c"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	rule.setProfiles("b,c:a,b");

	rule.execute(helper);
    }

    @Test(expected = EnforcerRuleException.class)
    public void testWhenNotMutuallyExclusiveProfilesWithRegex()
	    throws ExpressionEvaluationException, EnforcerRuleException {
	List<Profile> profiles = asList(createProfile("a"),
		createProfile("bigProfileName"));

	when(project.getActiveProfiles()).thenReturn(profiles);

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	rule.setProfiles("a,big*");

	rule.execute(helper);
    }
}
