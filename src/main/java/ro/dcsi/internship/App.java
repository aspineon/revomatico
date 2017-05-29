package ro.dcsi.internship;

import java.io.IOException;
import java.util.ArrayList;

public class App {

	public static void main(String[] args) throws IOException {
		ArrayList<User> users = new ArrayList<User>();
		new CsvExporter().export("src/main/resources/users100.csv", "target/users100out.csv");
		users = (ArrayList<User>) new CsvExporter().readUsers("target/users100out.csv");
	}
}
