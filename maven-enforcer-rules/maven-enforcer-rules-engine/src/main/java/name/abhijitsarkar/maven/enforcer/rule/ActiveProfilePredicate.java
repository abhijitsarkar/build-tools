package name.abhijitsarkar.maven.enforcer.rule;

import static org.apache.commons.collections4.CollectionUtils.exists;
import static org.apache.commons.collections4.PredicateUtils.invokerPredicate;
import static org.apache.commons.collections4.PredicateUtils.transformedPredicate;
import static org.apache.commons.collections4.TransformerUtils.invokerTransformer;

import java.util.Collection;

import org.apache.commons.collections4.Predicate;
import org.apache.maven.model.Profile;

public class ActiveProfilePredicate implements Predicate<String> {
    private final Collection<Profile> activeProfiles;

    public ActiveProfilePredicate(Collection<Profile> activeProfiles) {
	this.activeProfiles = activeProfiles;
    }

    @Override
    public boolean evaluate(final String profile) {
	return exists(
		activeProfiles,
		transformedPredicate(
			invokerTransformer("getId"),
			invokerPredicate("matches",
				new Class[] { String.class },
				new Object[] { profile })));
    }
}