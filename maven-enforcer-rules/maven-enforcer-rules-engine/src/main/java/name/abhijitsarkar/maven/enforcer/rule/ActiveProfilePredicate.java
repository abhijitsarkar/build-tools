package name.abhijitsarkar.maven.enforcer.rule;

import static org.apache.commons.collections4.CollectionUtils.exists;
import static org.codehaus.plexus.util.SelectorUtils.match;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.util.Collection;

import org.apache.commons.collections4.Predicate;

/**
 * Checks if a profile matches the name of any active profiles. Supports
 * wildcards in profile names. Null-safe.
 * 
 * @author Abhijit Sarkar
 *
 */
public class ActiveProfilePredicate implements Predicate<String> {
    private final Collection<String> profiles;

    public ActiveProfilePredicate(Collection<String> profiles) {
	this.profiles = profiles;
    }

    @Override
    public boolean evaluate(String activeProfile) {
	return exists(profiles, new Predicate<String>() {
	    @Override
	    public boolean evaluate(String profile) {
		return !isEmpty(activeProfile) && match(profile, activeProfile);
	    }
	});
    }
}