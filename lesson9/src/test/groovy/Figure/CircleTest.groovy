package Figure

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Killgur on 22.12.2015.
 */

@Unroll
class CircleTest extends Specification {
    def "Point In Circle"() {
        expect:
            pointInCircle == inCircle

        where:
            pointInCircle                               | inCircle
            new Circle( 0, 0, 5 ).pointInCircle( 3, 3 ) | true
            new Circle( 0, 0, 5 ).pointInCircle( 5, 5 ) | false
    }

    def "Square Of Circle"() {
        expect:
            squareOfCircle == square

        where:
            squareOfCircle               | square
            new Circle( 0, 0, 1 ).square | Math.PI
            new Circle( 0, 0, 0 ).square | 0
    }
}
