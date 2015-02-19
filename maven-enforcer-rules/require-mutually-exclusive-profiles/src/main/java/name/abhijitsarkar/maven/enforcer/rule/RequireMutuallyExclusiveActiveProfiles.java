package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections4.CollectionUtils.collect;
import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.TransformerUtils.invokerTransformer;
import static org.codehaus.plexus.util.StringUtils.defaultString;
import static org.codehaus.plexus.util.StringUtils.deleteWhitespace;
import static org.codehaus.plexus.util.StringUtils.isEmpty;
import static org.codehaus.plexus.util.StringUtils.join;
import static org.codehaus.plexus.util.StringUtils.trim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.Transformer;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class RequireMutuallyExclusiveActiveProfiles implements EnforcerRule {
    private static final CSVToListTransformer TRANSFORMER = new CSVToListTransformer();

    private String profiles;
    private String mutuallyExclusiveProfiles;

    public String getProfiles() {
	return profiles;
    }

    public void setProfiles(String profiles) {
	this.profiles = profiles;
    }

    public String getMutuallyExclusiveProfiles() {
	return mutuallyExclusiveProfiles;
    }

    public void setMutuallyExclusiveProfiles(String mutuallyExclusiveProfiles) {
	this.mutuallyExclusiveProfiles = mutuallyExclusiveProfiles;
    }

    @Override
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
	try {
	    MavenProject project = (MavenProject) helper.evaluate("${project}");

	    Collection<String> activeProfiles = collect(
		    project.getActiveProfiles(), invokerTransformer("getId"));

	    Collection<String> p1 = intersection(activeProfiles,
		    TRANSFORMER.transform(profiles));
	    Collection<String> p2 = intersection(activeProfiles,
		    TRANSFORMER.transform(mutuallyExclusiveProfiles));

	    Collection<String> commonActiveProfiles = intersection(p1, p2);

	    if (isNotEmpty(commonActiveProfiles)) {
		throw new EnforcerRuleException(
			"Found profiles that are not supposed to be active at the same time: "
				+ join(commonActiveProfiles.iterator(), ","));
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

    private static class CSVToListTransformer implements
	    Transformer<String, List<String>> {
	private static final String COMMA = ",";
	private static final String EMPTY = "";

	@Override
	public List<String> transform(final String csv) {
	    List<String> list = new ArrayList<>();

	    String csvTrimmed = deleteWhitespace(defaultString(trim(csv), EMPTY));

	    if (!isEmpty(csvTrimmed)) {
		list = asList(csv.split(COMMA));
	    }

	    return unmodifiableList(list);
	}
    }
}
