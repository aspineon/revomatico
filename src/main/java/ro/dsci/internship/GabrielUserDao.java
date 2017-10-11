package ro.dsci.internship;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class GabrielUserDao implements UserDao {

	@Override
	public List<User> readUsers(String locatie) {
		ArrayList<User> rezultat = new ArrayList<>();

		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

		CSVParser parser = null;
		try {
			parser = new CSVParser(new FileReader(locatie), format);
			for (CSVRecord record : parser) {
				User user = new User(record.get("id"), record.get("username"), record.get("firstname"),
						record.get("lastname"), record.get("email"));

				rezultat.add(user);
			}
		} catch (IOException e1) {

			throw new RuntimeException("Wrapped checked exception.", e1);
		}

		try {
			parser.close();
		} catch (IOException e) {

			 throw new RuntimeException("Wrapped checked exception.", e);
		}

		return rezultat;
	}

	@Override
	public void writeUsers(List<User> users, String locatie) {
		Object[] FILE_HEADER = { "id", "username", "firstname", "lastname", "email" };
		stergeDacaExista(locatie);
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
		try {
			fileWriter = new FileWriter(locatie);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			csvFilePrinter.printRecord(FILE_HEADER);
			for (User user : users) {
				List userDataRecord = new ArrayList();
				userDataRecord.add(user.id);
				userDataRecord.add(user.username);
				userDataRecord.add(user.firstname);
				userDataRecord.add(user.lastname);
				userDataRecord.add(user.email);
				csvFilePrinter.printRecord(userDataRecord);

			}
			System.out.println("CSV file was created successfully");

		} catch (IOException e) {

			 throw new RuntimeException("Wrapped checked exception.", e);
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();

			} catch (IOException e) {
				 throw new RuntimeException("Wrapped checked exception.", e);
			}

		}

	}

	private static List<String[]> toStringArray(List<User> users) {
		List<String[]> records = new ArrayList<String[]>();
		// add header record
		records.add(new String[] { "id", "username", "firstname", "lastname", "email" });
		Iterator<User> it = users.iterator();
		while (it.hasNext()) {
			User user = it.next();
			records.add(new String[] { user.id, user.username, user.firstname, user.lastname, user.email });
		}
		return records;
	}

	public void stergeDacaExista(String locatie) {
		Path p1 = Paths.get(locatie);
		try {
			Files.deleteIfExists(p1);
		} catch (IOException e) {
			 throw new RuntimeException("Wrapped checked exception.", e);
		}
	}

}