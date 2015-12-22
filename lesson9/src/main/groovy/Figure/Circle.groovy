package Figure

import groovy.transform.TupleConstructor

/**
 * Created by Killgur on 21.12.2015.
 * If get an error: Error:gradle-resources-test:lesson9: java.lang.NoClassDefFoundError: org/apache/tools/ant/util/ReaderInputStream
 * Then do in IDEA
 * File > Invalidate Caches / Restart...
 */

@TupleConstructor
class Circle extends Figure {
    def centerX
    def centerY
    def radius

    def pointInCircle( pointX, pointY ) {
        if ( Math.sqrt( ( centerX - pointX )**2 + ( centerY - pointY )**2 ) <= radius )
            return true
        return false
    }

    def getSquare(){
        Math.PI*radius**2
    }
}
