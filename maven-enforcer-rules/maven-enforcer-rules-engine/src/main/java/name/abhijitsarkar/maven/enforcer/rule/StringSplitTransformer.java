package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.codehaus.plexus.util.StringUtils.defaultString;
import static org.codehaus.plexus.util.StringUtils.deleteWhitespace;
import static org.codehaus.plexus.util.StringUtils.trim;

import java.util.List;

import org.apache.commons.collections4.Transformer;

public class StringSplitTransformer implements
	Transformer<String, List<String>> {
    private final String delimiter;
    private static final String EMPTY = "";

    public StringSplitTransformer(final String delimiter) {
	this.delimiter = delimiter;
    }

    @Override
    public List<String> transform(final String value) {
	String valueTrimmed = deleteWhitespace(defaultString(trim(value), EMPTY));

	return unmodifiableList(asList(valueTrimmed.split(delimiter)));
    }
}