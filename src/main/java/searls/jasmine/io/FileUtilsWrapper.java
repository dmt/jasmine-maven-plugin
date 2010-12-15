package searls.jasmine.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileUtilsWrapper {

	public String readFileToString(File file) throws IOException {
		return FileUtils.readFileToString(file);
	}

	public void forceMkdir(File file) throws IOException {
		FileUtils.forceMkdir(file);
	}

	@SuppressWarnings("unchecked")
	public Collection<File> listFiles(File file, String includes, String excludes) throws IOException {
		return org.codehaus.plexus.util.FileUtils.getFiles(file, includes, excludes);
	}

	public void writeStringToFile(File file, String data, String encoding) throws IOException {
		FileUtils.writeStringToFile(file, data, encoding);
	}

	public void copyDirectory(File srcDir, File destDir, IOFileFilter filter) throws IOException {
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}

}
