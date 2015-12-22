package Figure

import groovy.transform.TupleConstructor

/**
 * Created by Killgur on 22.12.2015.
 */

@TupleConstructor
class Triangle extends Figure {
    def sideA
    def sideB
    def sideC

    def isTriangle() {
        if ( sideA > 0 && sideB > 0 && sideC > 0 )
            if ( ( sideA + sideB > sideC ) && ( sideA + sideC > sideB ) && ( sideB + sideC > sideA ) )
                return true
        return false
    }

    def isEquilateralTriangle() {
        if ( isTriangle() )
            if ( ( sideA == sideB ) && ( sideA == sideC ) && ( sideB == sideC ) )
                return true
        return false
    }

    def getSquare(){
        def hafPerimeter = ( sideA + sideB + sideC )/2
        Math.sqrt( hafPerimeter * ( hafPerimeter - sideA ) * ( hafPerimeter - sideB ) * ( hafPerimeter - sideC ) )
    }
}
