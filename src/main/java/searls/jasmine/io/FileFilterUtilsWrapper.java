package searls.jasmine.io;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileFilterUtilsWrapper {

	public IOFileFilter suffixFileFilter(String suffix) {
		return FileFilterUtils.suffixFileFilter(suffix);
	}

	public IOFileFilter and(IOFileFilter... filters) {
		return FileFilterUtils.and(filters);
	}

	public IOFileFilter or(IOFileFilter... filters) {
		return FileFilterUtils.or(filters);
	}
}	
