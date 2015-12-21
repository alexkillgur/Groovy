/**
 * Created by Killgur on 21.12.2015.
 */
class CircleTest extends spock.lang.Specification {
    def "Point In Circle"() {
        expect:
            pointInCircle == inCircle

        where:
            pointInCircle                               | inCircle
            new Circle( 0, 0, 5 ).pointInCircle( 3, 3 ) | true
            new Circle( 0, 0, 5 ).pointInCircle( 5, 5 ) | false
    }
}
