/**
 * Created by Killgur on 21.12.2015.
 */
class CircleTest extends spock.lang.Specification {
    def "PopintInCircle"() {
        expect:
            pointInCircle == true

        where:
            pointInCircle = new Circle( 0, 0, 5 ).popintInCircle( 3, 3 )
    }
}
