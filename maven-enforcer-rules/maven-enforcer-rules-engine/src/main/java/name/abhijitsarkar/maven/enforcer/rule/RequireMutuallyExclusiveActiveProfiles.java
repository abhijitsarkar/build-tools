package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections4.CollectionUtils.collect;
import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.apache.commons.collections4.TransformerUtils.invokerTransformer;
import static org.codehaus.plexus.util.StringUtils.defaultString;
import static org.codehaus.plexus.util.StringUtils.deleteWhitespace;
import static org.codehaus.plexus.util.StringUtils.join;
import static org.codehaus.plexus.util.StringUtils.trim;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.Transformer;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
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
	try {
	    MavenProject project = (MavenProject) helper.evaluate("${project}");

	    Collection<String> activeProfiles = collect(
		    project.getActiveProfiles(), invokerTransformer("getId"));

	    Collection<String> mutuallyExclusiveProfiles = COLON_SPLIT_TRANSFORMER
		    .transform(profiles);

	    Collection<String> p = null;

	    for (String profile : mutuallyExclusiveProfiles) {
		p = intersection(activeProfiles,
			COMMA_SPLIT_TRANSFORMER.transform(profile));
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

    static class StringSplitTransformer implements
	    Transformer<String, List<String>> {
	private final String delimiter;
	private static final String EMPTY = "";

	StringSplitTransformer(final String delimiter) {
	    this.delimiter = delimiter;
	}

	@Override
	public List<String> transform(final String value) {
	    String valueTrimmed = deleteWhitespace(defaultString(trim(value),
		    EMPTY));

	    return unmodifiableList(asList(valueTrimmed.split(delimiter)));
	}
    }
}
