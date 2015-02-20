package name.abhijitsarkar.maven.enforcer.rule;

import org.apache.maven.model.Profile;

public class TestUtil {
    public static Profile createProfile(String profileId) {
	Profile p = new Profile();
	p.setId(profileId);

	return p;
    }
}
