package com.box;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class DirMonitor {
	private Path rec = null;//The attribute to be used in anonymous class
	private final Path dir;
	
	interface MyAction {
		void perform(Path p) throws IOException;
	}


	
/**
 * The constructor for the DirMonitor class
 * @param dir is a directory in the file system
 * @throws IOException
 */
	public DirMonitor(Path dir) throws IOException {
		if (!Files.isDirectory(dir) || !Files.isReadable(dir))
			throw new IOException();
		this.dir = dir;
	}

	/**
	 * printFiles goes though all the files in a directory and print their names
	 * @throws IOException
	 */
	public void printFiles() throws IOException {
		for (Path p : Files.newDirectoryStream(dir)) {
			System.out.println(p.getFileName());
		}
	}
	
	public void printFilesLambda() throws IOException{
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir);
		dirStream.forEach(p -> System.out.println(p.getFileName()));
	}
/**
 * printFilesFilter is a method that traverse through the directory and 
 * print all files based on a sizeBound
 * @param sizeBound is used to create a filter(object of inner class SizeFilter)
 * @throws IOException
 */
	public void printFilesFilter(int sizeBound) throws IOException {
		DirectoryStream.Filter<Path> filter = new SizeFilter(sizeBound);
		for (Path p : Files.newDirectoryStream(dir, filter)) {
			System.out.println(p);
		}
	}
	
	public void printFilesFilterlambda(int sizeBound) throws IOException {
		//DirectoryStream.Filter<Path> filter = new SizeFilter(sizeBound);
		Files.newDirectoryStream(dir, c->Files.size(c) > sizeBound).forEach(p ->System.out.println(p));
		}
		
/**
 * Return the total size of files in a directory
 * @return
 * @throws IOException
 */
	
	public long sizeOfFiles() throws IOException {
		long s = 0;

		for (Path p : Files.newDirectoryStream(dir)) {
			if (!Files.isDirectory(p))
				s += Files.size(p);
		}
		System.out.println(s);
		return s;
	}
	

	
/**
 * Finding the most recent file	
 * @return
 * @throws IOException
 */
	public Path mostRecent() throws IOException {
		Path rec = null;

		FileTime last = FileTime.fromMillis(0);
		for (Path p : Files.newDirectoryStream(dir)) {
			FileTime pT = Files.getLastModifiedTime(p);
			if (pT.compareTo(last) > 0) {
				last = pT;
				rec = p;
			}
		}
		return rec;
	}
	
//*************************************************************//	
	/**
	 * The method SizeFilter is an inner class that defines a filter based on size
	 * @author hosseinkhani
	 *This method implements DirectoryStream.Filter<Path> and override method accept
	 */
		class SizeFilter implements DirectoryStream.Filter<Path> {
			private final int sizeBound;

			public SizeFilter(int sizeBound) {
				this.sizeBound = sizeBound;
			}

			@Override
			public boolean accept(Path entry) throws IOException {
				
					return Files.size(entry) >= sizeBound;
			}
		}
	/**
	 * PrefixFilter defines a filter to find the filenames that end by a prefix.
	 * @author hosseinkhani
	 *
	 */
		class PrefixFilter implements DirectoryStream.Filter<Path> {
			private final String prefix;

			public PrefixFilter(String prefix) {
				this.prefix = prefix;
			}

			@Override
			public boolean accept(Path entry) throws IOException {
				// TODO Auto-generated method stub
				return entry.endsWith(".txt");
			}

		}
	
	
	
	/**
	 * Implementation of method printFilesFilter with anonymous classes.
	 * @param sizeBound
	 * @throws IOException
	 */
	public void printFilesFilter2(int sizeBound) throws IOException {
		// DirectoryStream.Filter<Path> filter = new SizeFilter(sizeBound);
		for (Path p : Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.size(entry) >= sizeBound;
			}

		})) {
			System.out.println(p);
		}
	}	
	
/**
 * printing file names based on a new filter prefixFilter.
 * @param prefix
 */
	public void printFilesFilter25(String prefix) throws IOException {
		for (Path p: Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path entry) throws IOException {
				// TODO Auto-generated method stub
				return entry.endsWith(prefix);
			}
			
		}))
		{
			System.out.println(p);
		}
	}
//********************************************************************//	
	
	public void applyAction(String prefix, MyAction action) throws IOException {
		// DirectoryStream.Filter<Path> filter = new SizeFilter(sizeBound);

		for (Path p : Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path entry) throws IOException {
				// TODO Auto-generated method stub
				return entry.startsWith(prefix);
			} 

		})) {
			action.perform(p);
		}
	}
/**
 * The method printFiles3 look for the files that start with a specific prefix and have
 * a size greater or equal to sizeBound
 * @param sizeBound
 * @throws IOException
 */
	public void printFiles3(int sizeBound,String prefix) throws IOException {
		applyAction(prefix, new MyAction() {
			@Override
			public void perform(Path p) throws IOException {
				if (Files.size(p) >= sizeBound) {
					System.out.println(p.getFileName());
				}
			}
		});
	}



	class SizeAction implements MyAction {
		private int s = 0;

		@Override
		public void perform(Path p) throws IOException {
			s += Files.size(p);

		}

	}

	public long sizeOfFiles1(String prefix) throws IOException {
		SizeAction sa = new SizeAction();
		applyAction(prefix, sa);
		return sa.s;
	}

/**
 * It is not possible to define the method sizeOfFiles with an anonymous class because it
 * requires to change the value of local variable.
 * @param prefix
 * @return
 * @throws IOException
 */
	


	public long sizeOfFiles2(String prefix) throws IOException {
		// SizeAction sa = new SizeAction();
		int s = 0;
		applyAction(prefix, new MyAction() {

			@Override
			public void perform(Path p) throws IOException {

				//s += Files.size(p);

			}

		});
		return s;
	}

/**
 * (Extra) method to search for files that start with a prefix and recently modified
 * @param sizeBound
 * @param prefix
 * @return
 * @throws IOException
 */


	public Path mostRecent2(int sizeBound,String prefix) throws IOException {
		class ActionRecent implements MyAction {
			private Path rec = null;
			private FileTime last = FileTime.fromMillis(0);

			@Override
			public void perform(Path p) throws IOException {
				FileTime pT = Files.getLastModifiedTime(p);
				if (pT.compareTo(last) > 0) {
					last = pT;
					rec = p;
				}
			}
		}
		ActionRecent r = new ActionRecent();
		applyAction(prefix, r);
		return r.rec;
		
	}
	 public Path mostRecent25(int sizeBound, String prefix) throws IOException{	
		applyAction(prefix , new MyAction() {
			
			private FileTime last = FileTime.fromMillis(0);
			@Override
			public void perform(Path p) throws IOException {
                
                FileTime pT = Files.getLastModifiedTime(p);
				if (Files.size(p) > sizeBound && pT.compareTo(last) > 0  ) {
					last = pT;
					rec = p;
					
				}
				
			}
			
		});
		
		return rec;
	}
	



	public static void main(String[] args) throws IOException {
		DirMonitor lf = new DirMonitor(Paths.get("."));

		//lf.printFilesLambda();
        lf.printFilesFilterlambda(150);
		//System.out.println(lf.sizeOfFiles());

		//System.out.println(lf.mostRecent());
		//System.out.println("filtering:");
		lf.printFilesFilter(150);

		//System.out.println("new print");
		//lf.printFiles3(100);
		//System.out.println(lf.sizeOfFiles2(100));
		//System.out.println(lf.mostRecent2(100));
	}

}