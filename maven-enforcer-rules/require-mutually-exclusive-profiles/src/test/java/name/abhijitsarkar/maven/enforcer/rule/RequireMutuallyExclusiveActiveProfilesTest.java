package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import mockit.Expectations;
import mockit.Mocked;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Test;

public class RequireMutuallyExclusiveActiveProfilesTest {
    @Test(expected = EnforcerRuleException.class)
    public void testWhenNoCommonProfiles(@Mocked EnforcerRuleHelper helper,
	    @Mocked MavenProject project) throws ExpressionEvaluationException,
	    EnforcerRuleException {
	new Expectations() {
	    {
		helper.evaluate("${project}");
		returns(project);

		project.getActiveProfiles();
		returns(asList("a", "b"));
	    }
	};

	RequireMutuallyExclusiveActiveProfiles rule = new RequireMutuallyExclusiveActiveProfiles();
	rule.setProfiles("a,c");
	rule.setMutuallyExclusiveProfiles("b,c");

	rule.execute(helper);
    }
}
