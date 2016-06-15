package name.abhijitsarkar.gradle

import spock.lang.Specification


/**
 * @author Abhijit Sarkar
 */
class HelloWorldSpec extends Specification {
    def "tests that the world can be saved, takes a long time"() {
        expect:
        new HelloWorld().sayHello() == 'hello'
    }
}