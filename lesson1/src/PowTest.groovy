/**
 * Created by Killgur on 22.10.2015.
 */
class PowTest extends GroovyTestCase {
    
    def a = 60
    def b = 98
    def power = a**b
    def powerMath = Math.pow(a, b)
    def powerLog = Math.exp(b*Math.log(a))
    
    void testClassName() {
        assert power.class == BigInteger
        assert powerMath.class == Double
        assert powerLog.class == Double
    }
    
    void testResult() {
        assert power == 1814773954166863628046361853216827279269843640202652420952977684359714281881600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        assert powerMath == 1.8147739541668635e174
        assert powerLog == 1.8147739541668317e174
    }
}
