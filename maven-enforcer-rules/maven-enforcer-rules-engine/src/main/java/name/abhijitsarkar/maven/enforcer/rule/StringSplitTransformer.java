package name.abhijitsarkar.maven.enforcer.rule;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.codehaus.plexus.util.StringUtils.deleteWhitespace;

import java.util.List;

import org.apache.commons.collections4.Transformer;

public class StringSplitTransformer implements
	Transformer<String, List<String>> {
    private final String delimiter;

    public StringSplitTransformer(String delimiter) {
	this.delimiter = delimiter;
    }

    @Override
    public List<String> transform(String value) {
	String valueTrimmed = deleteWhitespace(value);

	return unmodifiableList(asList(valueTrimmed.split(delimiter)));
    }
}