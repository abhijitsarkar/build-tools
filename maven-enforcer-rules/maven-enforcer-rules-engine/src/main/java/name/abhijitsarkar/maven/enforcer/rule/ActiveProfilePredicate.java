package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.exists;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.union;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static org.codehaus.plexus.util.SelectorUtils.match;

import java.util.Collection;

import org.apache.commons.collections4.Predicate;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

public class ActiveProfilePredicate implements Predicate<String> {
    private final Collection<Profile> activeProfiles;

    public ActiveProfilePredicate(MavenProject project) {
	this.activeProfiles = getActiveProfiles(project);
    }
    
    public Collection<Profile> getActiveProfiles() {
        return activeProfiles;
    }

    @Override
    public boolean evaluate(String profile) {
	return exists(activeProfiles, new Predicate<Profile>() {
	    @Override
	    public boolean evaluate(Profile activeProfile) {
		return match(profile, activeProfile.getId());
	    }
	});
    }

    private Collection<Profile> getActiveProfiles(MavenProject project) {
	Collection<Profile> activeProfiles = project.getActiveProfiles();
	activeProfiles = isNotEmpty(activeProfiles) ? activeProfiles
		: emptyList();

	MavenProject parentProject = project.getParent();

	activeProfiles = parentProject == null ? activeProfiles : union(
		activeProfiles, getActiveProfiles(parentProject));

	return unmodifiableCollection(activeProfiles);
    }
}