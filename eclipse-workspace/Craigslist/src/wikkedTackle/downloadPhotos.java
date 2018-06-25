package wikkedTackle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class downloadPhotos
{
	// Creates the file and the directory
	public static File createNewFile() throws IOException
	{
		File file = new File("/Users/Kamaro/Downloads/spreadsheet-modified.csv");
		File destinationDir = new File("/Users/Kamaro/Downloads/test-directory/");

		FileUtils.copyFileToDirectory(file, destinationDir);

		File newFile = new File(file.toString().replaceAll("spreadsheet-modified.csv", "/test-directory/spreadsheet-modified-new.csv"));
		clearFile(newFile);

		writeFileAsAppend(file, newFile);
		return newFile;
	}

	// Copies the lines that are read by readFileAsSequencesOfLines()
	private static void writeFileAsAppend(File reader, File writer) throws IOException
	{
		List<String> lines = readFileAsSequencesOfLines(reader);
		Path path = writer.toPath();
		Files.write(path, lines, StandardOpenOption.APPEND);
	}

	// Reads the file line for line
	private static List<String> readFileAsSequencesOfLines(File file) throws IOException
	{
		Path path = file.toPath();
		return Files.readAllLines(path);
	}

	// Empty-file check
	public static void checkIfEmpty(File file) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		if (br.readLine() == null)
		{
			System.out.println("No errors, and file empty");
		}
		else
		{
			System.out.println("Not empty");
		}
	}

	// Removes all content from the file
	public static void clearFile(File file) throws IOException
	{
		PrintWriter writer = new PrintWriter(file);
		writer.close();
	}

	private static String getField(String line, int i)
	{
		return line.split(",")[i];// extract value you want to sort on
	}

	// Sorts the file in ascending order.
	public static String[] sortFile(File file) throws Exception
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Map<String, List<String>> map = new TreeMap<>();
		String[] imageURLs = new String[15000];
		int count = 0;
		String line = reader.readLine();//read header
		while ((line = reader.readLine()) != null)
		{

			String key = getField(line, 1);

			List<String> l = map.get(key);
			if (l == null)
			{
				l = new LinkedList<>();
				map.put(key, l);
			}
			imageURLs[count] = key;
			String key2 = getField(line, 0);
			l.add(line.substring(0, key2.length() + key.length() + 1));
			if (!key.isEmpty() && key != " " && key != "" && key.contains("//") && key != "Spinner Baits")
			{
				System.out.println("File: " + key);
				downloadImages(key, key2);

			}
			count++;
		}
		reader.close();
		FileWriter writer = new FileWriter(file);
		writer.write("SKU, IMAGE, TITLE, MAIN CATEGORY\n");
		for (List<String> list : map.values())
		{
			for (String val : list)
			{
				writer.write(val);
				writer.write("\n");
			}
		}
		writer.close();
		return imageURLs;
	}

	// Downloads the photos using the URLs in the csv
	public static void downloadImages(String image, String name) throws Exception
	{
		int count = 5;
		FileOutputStream fos = null;
		URL website = new URL(image);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		fos = new FileOutputStream("/Volumes/Mac/test/" + name.replaceAll("/", "-") + ".jpg");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}

	public static void main(String[] args) throws IOException, Exception
	{
		Logger LOGGER = Logger.getLogger("InfoLogging");
		LOGGER.info("Logging an INFO-level message");
		File newFile;
		newFile = createNewFile();
		String[] imageURLs = sortFile(newFile);
		// downloadImages(imageURLs);
	}

}
