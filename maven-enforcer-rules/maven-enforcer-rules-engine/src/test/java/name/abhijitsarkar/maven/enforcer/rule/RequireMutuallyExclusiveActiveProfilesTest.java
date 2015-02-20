package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class RequireMutuallyExclusiveActiveProfilesTest {
    @Test(expected = EnforcerRuleException.class)
    public void testWhenNotMutuallyExclusiveProfiles(
	    @Mocked EnforcerRuleHelper helper, @Mocked MavenProject project,
	    @Mocked Profile a, @Mocked Profile b)
	    throws ExpressionEvaluationException, EnforcerRuleException {
	new Expectations() {
	    {
		helper.evaluate("${project}");
		returns(project);

		a.getId();
		returns("a");

		b.getId();
		returns("b");

		project.getActiveProfiles();
		returns(asList(a, b));
	    }
	};

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	/* Throw in some random spaces and see if the rule can handle it. */
	rule.setProfiles("b, c: a,b");

	rule.execute(helper);
    }

    @Test
    public void testWhenMutuallyExclusiveProfiles(
	    @Mocked EnforcerRuleHelper helper, @Mocked MavenProject project,
	    @Mocked Profile a, @Mocked Profile c)
	    throws ExpressionEvaluationException, EnforcerRuleException {
	new Expectations() {
	    {
		helper.evaluate("${project}");
		returns(project);

		a.getId();
		returns("a");

		c.getId();
		returns("c");

		project.getActiveProfiles();
		returns(asList(a, c));
	    }
	};

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	rule.setProfiles("b,c:a,b");

	rule.execute(helper);
    }
}
