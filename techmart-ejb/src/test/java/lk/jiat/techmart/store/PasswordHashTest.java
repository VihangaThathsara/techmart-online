package lk.jiat.techmart.store;
import lk.jiat.techmart.util.PasswordHash;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PasswordHashTest {
 @Test void correctPasswordAndSalt() {
  String a = PasswordHash.hash("local-test-password");
  String b = PasswordHash.hash("local-test-password");
  assertNotEquals(a, b);
  assertTrue(PasswordHash.verify("local-test-password", a));
  assertFalse(PasswordHash.verify("wrong", a));
 }
 @Test void rejectsPlaintextAndMalformed() {
  assertFalse(PasswordHash.verify("1234", "1234"));
  assertFalse(PasswordHash.verify("1234", "pbkdf2$bad$x$y"));
  assertFalse(PasswordHash.verify(null, null));
 }
}
