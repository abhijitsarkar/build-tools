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

/**
 * Rule that fails if any of the given combination pf profiles are active,
 * including inherited ones. The 'profiles' string can contain several
 * combinations separated by colon (:), where each combination is a
 * comma-separated list. For example, p1,p2:p1,p3 means that profile p1 can't be
 * active with either of profiles p2 or p3. Wildcards (*) in profile names are
 * supported too.
 * 
 * @author Abhijit Sarkar
 *
 */
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
	    Collection<String> activeProfiles = new ActiveProfilesTransformer()
		    .transform(project);

	    logger.debug("Input profiles: " + profiles);

	    Collection<String> mutuallyExclusiveProfiles = COLON_SPLIT_TRANSFORMER
		    .transform(profiles);

	    logger.debug("After colon split: " + mutuallyExclusiveProfiles);

	    Collection<String> p = null;

	    for (String profile : mutuallyExclusiveProfiles) {
		p = COMMA_SPLIT_TRANSFORMER.transform(profile);

		logger.debug("After comma split: " + p);

		p = select(activeProfiles, new ActiveProfilePredicate(p));

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
