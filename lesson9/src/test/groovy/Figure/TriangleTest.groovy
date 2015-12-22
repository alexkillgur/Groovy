package Figure

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Killgur on 22.12.2015.
 */

@Unroll
class TriangleTest extends Specification {
    def "Is Triangle"() {
        expect:
            triangle == isTriangle

        where:
            triangle                              | isTriangle
            new Triangle( 3, 4, 5 ).isTriangle()  | true
            new Triangle( -3, 4, 5 ).isTriangle() | false
            new Triangle( 2, 1, 1 ).isTriangle()  | false
    }

    def "Is Equilateral Triangle"() {
        expect:
            triangle == isEquilateralTriangle

        where:
            triangle                                         | isEquilateralTriangle
            new Triangle( 3, 3, 3 ).isEquilateralTriangle()  | true
            new Triangle( -3, 3, 3 ).isEquilateralTriangle() | false
            new Triangle( 3, 4, 5 ).isEquilateralTriangle()  | false
    }

    def "Square Of Triangle"() {
        expect:
            squareOfTriangle == square

        where:
            squareOfTriangle               | square
            new Triangle( 2, 2, 2 ).square | Math.sqrt( 3 )
            new Triangle( 3, 4, 5 ).square | 6.0
    }
}
