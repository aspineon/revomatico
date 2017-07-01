package ro.dcsi.internship;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Test;

public class ForgeRockDbTest {
  /* TODO add required field checks */
  private static String openIdmServer = "http://localhost:8080";
  private static String openIdmUsername = "openidm-admin";
  private static String openIdmPassword = "openidm-admin";
  private static String existingUserId = "ForgeRockDBTestExistingUser";
  private static String nonExistingUserId = "ForgeRockDBTestNonExistingUser";
  
  @AfterClass
  public static void prepareDatabase() {
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    db.deleteUser(nonExistingUserId);
    Hashtable<String, String> existingUserAttributes = new Hashtable<String, String>();
    existingUserAttributes.put("_id", existingUserId);
    existingUserAttributes.put("userName", existingUserId);
    existingUserAttributes.put("mail", "ExistingUser@ex.com");
    existingUserAttributes.put("sn", existingUserId);
    existingUserAttributes.put("givenName", existingUserId);
    User existingUser = new User(existingUserId, existingUserAttributes);
    db.addUser(existingUser);
  }
  
  @Test
  public void prepareDatabaseTest() {
    /* TODO general tests */
    ForgeRockDbTest.prepareDatabase();
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    Optional<User> user = db.getUser(existingUserId);
    assertTrue(user.isPresent());
    assertEquals(existingUserId, db.getUser(existingUserId).get().getId());
  }

  @Test
  public void getUserTest() {
    /* TODO general tests */
    ForgeRockDbTest.prepareDatabase();
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    Optional<User> user = db.getUser(existingUserId);
    assertTrue(user.isPresent());
    assertEquals(existingUserId, user.get().getAttributeValue("sn"));

    Optional<User> user2 = db.getUser(nonExistingUserId);
    assertFalse(user2.isPresent());
  }

  @Test
  public void userExistsTest() {
    /* TODO general tests */
    ForgeRockDbTest.prepareDatabase();
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    assertTrue(db.userExists(existingUserId));
    assertFalse(db.userExists(nonExistingUserId));
  }

  @Test
  public void deleteUserTest() {
    /* TODO general tests */
    ForgeRockDbTest.prepareDatabase();
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    assertTrue(db.deleteUser(existingUserId));
    assertFalse(db.deleteUser(nonExistingUserId));
  }

  @Test
  public void updateUserTest() {
    /* TODO general tests */
    /* TODO update nonExistingUser */
    ForgeRockDbTest.prepareDatabase();
    Hashtable<String, String> attributes = new Hashtable<String, String>();
    attributes.put("_id", existingUserId);
    attributes.put("mail", "ExistingUser@ex.com");
    attributes.put("sn", existingUserId);
    attributes.put("givenName", existingUserId);
    attributes.put("userName", "joe");
    attributes.put("city", "Bucharest");
    attributes.put("customAttr", "customVal");
    User user = new User(existingUserId, attributes);
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    assertTrue(db.updateUser(user));
    assertTrue(db.getUser(existingUserId).isPresent());
    assertEquals("joe", db.getUser(existingUserId).get().getAttributeValue("userName"));
    assertEquals("Bucharest", db.getUser(existingUserId).get().getAttributeValue("city"));
    assertEquals("customVal", db.getUser(existingUserId).get().getAttributeValue("customAttr"));
  }
  
  @Test
  public void addUserTest() {
    /* TODO add existingUser */
    ForgeRockDbTest.prepareDatabase();
    Hashtable<String, String> attributes = new Hashtable<String, String>();
    attributes.put("_id", nonExistingUserId);
    attributes.put("mail", "NonExistingUser@ex.com");
    attributes.put("sn", nonExistingUserId);
    attributes.put("givenName", nonExistingUserId);
    attributes.put("userName", nonExistingUserId);
    User user = new User(nonExistingUserId, attributes);
    ForgeRockDb db = new ForgeRockDb(openIdmServer, openIdmUsername, openIdmPassword);
    assertTrue(db.addUser(user));
    assertTrue(db.getUser(nonExistingUserId).isPresent());
    assertEquals(nonExistingUserId, db.getUser(nonExistingUserId).get().getAttributeValue("userName"));
  }
}
