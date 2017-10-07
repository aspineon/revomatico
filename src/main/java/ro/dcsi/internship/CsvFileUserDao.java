package ro.dcsi.internship;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFileUserDao implements FileUserDao {

  public CsvFileUserDao() {
    System.out.println("instantiated");
  }

  @Override
	public String loadHeader(String csvFile) {
		String csvSplitBy = ",";
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line = "";
			return br.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

  @Override
  public List<User> load(String csvFile) {
    String csvSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
      ArrayList<User> users = new ArrayList<>();
      String line = "";
      Header header = new Header(br.readLine().split(csvSplitBy));
      while ((line = br.readLine()) != null) {
        String[] rowValues = line.split(csvSplitBy);
        users.add(header.toUser(rowValues));
      }
      return users;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(List<User> users, String csvFile) {
    Header header = new Header();
    try (FileWriter fo = new FileWriter(csvFile); BufferedWriter out = new BufferedWriter(fo)) {
      out.write(header.defaultHeader + "\n");
      for (User user : users) {
        out.write(Arrays.toString(header.fromUser(user)) + "\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
