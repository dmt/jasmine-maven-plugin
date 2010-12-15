package searls.jasmine.io;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryCopierTest {

	@InjectMocks private DirectoryCopier directoryCopier = new DirectoryCopier();
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private FileFilterUtilsWrapper fileFilterUtilsWrapper;
	
	@Mock private File srcDir;
	@Mock private File destDir;
	
	@Test
	public void shouldBuildSuffixFilter() throws IOException {
		String suffixFilter = ".js";
		
		directoryCopier.copyDirectory(srcDir,destDir,suffixFilter);

		verify(fileFilterUtilsWrapper).suffixFileFilter(suffixFilter);
	}
	
	@Test
	public void shouldAssignSuffixFilterAsFileFilter() throws IOException {
		IOFileFilter expected = stubSuffixFilter();
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verify(fileFilterUtilsWrapper).and(FileFileFilter.FILE, expected);
	}

	@Test
	public void shouldApplyDirectoriesToFilterAfterFileFilter() throws IOException {
		IOFileFilter suffixFilter = stubSuffixFilter();
		IOFileFilter expected = stubAndFilter(suffixFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verify(fileFilterUtilsWrapper).or(DirectoryFileFilter.DIRECTORY, expected);
	}

	@Test
	public void shouldApplyVisibleToFilter() throws IOException {
		IOFileFilter suffixFilter = stubSuffixFilter();
		IOFileFilter fileFilter = stubAndFilter(suffixFilter);
		IOFileFilter dirFilter = stubOrFilter(fileFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verify(fileFilterUtilsWrapper).and(HiddenFileFilter.VISIBLE, dirFilter);
	}
	
	@Test
	public void shouldCopyDirectory() throws IOException {
		IOFileFilter suffixFilter = stubSuffixFilter();
		IOFileFilter fileFilter = stubAndFilter(suffixFilter);
		IOFileFilter dirFilter = stubOrFilter(fileFilter);
		IOFileFilter visibilityFilter = stubAndFilter(dirFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".something");
		
		verify(fileUtilsWrapper).copyDirectory(srcDir, destDir, visibilityFilter);
	}

	private IOFileFilter stubOrFilter(IOFileFilter fileFilter) {
		IOFileFilter orFilter = mock(IOFileFilter.class);
		when(fileFilterUtilsWrapper.or(eq(DirectoryFileFilter.DIRECTORY), eq(fileFilter))).thenReturn(orFilter);
		return orFilter;
	}
	
	private IOFileFilter stubSuffixFilter() {
		IOFileFilter expected = mock(IOFileFilter.class);
		when(fileFilterUtilsWrapper.suffixFileFilter(anyString())).thenReturn(expected);
		return expected;
	}
	
	private IOFileFilter stubAndFilter(IOFileFilter first) {
		IOFileFilter andResult = mock(IOFileFilter.class);
		when(fileFilterUtilsWrapper.and(isA(IOFileFilter.class), eq(first))).thenReturn(andResult);
		return andResult;
	}
}
