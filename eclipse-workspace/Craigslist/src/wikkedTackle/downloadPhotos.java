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

import org.apache.commons.io.FileUtils;

public class downloadPhotos
{
	public static File createNewFile() throws IOException
	{
		File file = new File("/Users/Kamaro/Downloads/spreadsheet-modified.csv");
		File destinationDir = new File("/Users/Kamaro/Downloads/test-directory/");

		FileUtils.copyFileToDirectory(file, destinationDir);

		File newFile = new File(file.toString().replaceAll("spreadsheet-modified.csv", "/test-directory/spreadsheet-modified-new.csv"));
		clearFile(newFile);

		writeFileAsAppend(file, newFile);
		// checkIfEmpty(newFile);
		return newFile;
	}

	private static void writeFileAsAppend(File reader, File writer) throws IOException
	{
		List<String> lines = readFileAsSequencesOfLines(reader);
		Path path = writer.toPath();
		Files.write(path, lines, StandardOpenOption.APPEND);
	}

	private static List<String> readFileAsSequencesOfLines(File file) throws IOException
	{
		Path path = file.toPath();
		List<String> lines = Files.readAllLines(path);
		// System.out.println(lines.toString());
		return lines;
	}

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

	public static void clearFile(File file) throws IOException
	{
		PrintWriter writer = new PrintWriter(file);
		writer.close();
	}

	private static String getField(String line, int i)
	{
		return line.split(",")[i];// extract value you want to sort on
	}

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
			List<String> l2 = map.get(key2);
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
				//System.out.println(val);
			}
		}
		writer.close();
		return imageURLs;
	}

	public static void downloadImages(String image, String name) throws Exception
	{
		//Arrays.sort(images);
		int count = 5;
		FileOutputStream fos = null;
		//System.out.println(image);
		URL website = new URL(image);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		fos = new FileOutputStream("/Volumes/Mac/test/" + name.replaceAll("/", "-") + ".jpg");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}

	public static void main(String[] args) throws IOException, Exception
	{
		File newFile;
		newFile = createNewFile();
		// System.out.println("File created: " + newFile.toString());
		String[] imageURLs = sortFile(newFile);
		// downloadImages(imageURLs);
		// System.out.println("Done.");
	}

}
