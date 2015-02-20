package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.collect;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.union;
import static org.apache.commons.collections4.TransformerUtils.invokerTransformer;

import java.util.Collection;

import org.apache.commons.collections4.Transformer;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

/**
 * Transforms a maven project to a collection of active profile names, including
 * inherited profiles.
 * 
 * @author Abhijit Sarkar
 *
 */
public class ActiveProfilesTransformer implements
	Transformer<MavenProject, Collection<String>> {
    @Override
    public Collection<String> transform(MavenProject project) {
	Collection<Profile> projectActiveProfiles = project.getActiveProfiles();
	projectActiveProfiles = isNotEmpty(projectActiveProfiles) ? projectActiveProfiles
		: emptyList();

	MavenProject parentProject = project.getParent();

	Collection<String> activeProfiles = collect(projectActiveProfiles,
		invokerTransformer("getId"));

	return parentProject == null ? activeProfiles : union(activeProfiles,
		transform(parentProject));
    }
}
