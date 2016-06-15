package name.abhijitsarkar.gradle;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Abhijit Sarkar
 */
public class SayCheeseTest {
    @Test
    public void testSaysCheese() {
        assertThat(new SayCheese().sayCheese(), is("cheese"));
    }
}