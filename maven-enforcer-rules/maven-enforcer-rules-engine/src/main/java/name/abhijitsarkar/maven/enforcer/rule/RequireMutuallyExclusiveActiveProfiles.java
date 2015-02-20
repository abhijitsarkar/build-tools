package name.abhijitsarkar.maven.enforcer.rule;

import static org.apache.commons.collections4.CollectionUtils.select;
import static org.codehaus.plexus.util.StringUtils.join;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.Transformer;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class RequireMutuallyExclusiveActiveProfiles implements EnforcerRule {
    private static final Transformer<String, List<String>> COLON_SPLIT_TRANSFORMER = new StringSplitTransformer(
	    ":");
    private static final Transformer<String, List<String>> COMMA_SPLIT_TRANSFORMER = new StringSplitTransformer(
	    ",");

    private String profiles;

    public String getProfiles() {
	return profiles;
    }

    public void setProfiles(String profiles) {
	this.profiles = profiles;
    }

    @Override
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
	Log logger = helper.getLog();

	try {
	    MavenProject project = (MavenProject) helper.evaluate("${project}");

	    logger.debug("Input profiles: " + profiles);

	    Collection<String> mutuallyExclusiveProfiles = COLON_SPLIT_TRANSFORMER
		    .transform(profiles);

	    logger.debug("After colon split: " + mutuallyExclusiveProfiles);

	    Collection<String> p = null;

	    for (String profile : mutuallyExclusiveProfiles) {
		p = COMMA_SPLIT_TRANSFORMER.transform(profile);

		logger.debug("After comma split: " + p);

		p = select(p, new ActiveProfilePredicate(project));

		logger.debug("After selecting only active profiles: " + p);

		if (p.size() >= 2) {
		    throw new EnforcerRuleException(
			    "Found profiles that are not supposed to be active at the same time: "
				    + join(p.iterator(), ","));
		}
	    }
	} catch (ExpressionEvaluationException e) {
	    throw new EnforcerRuleException("Unable to lookup an expression "
		    + e.getMessage(), e);
	}
    }

    @Override
    public String getCacheId() {
	return "anything";
    }

    @Override
    public boolean isCacheable() {
	return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule arg0) {
	return false;
    }
}
