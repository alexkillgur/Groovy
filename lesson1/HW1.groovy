/**
 * Created by Killgur on 16.10.2015.
 * First homework
 */

println '''
=============================================================================================
                            ����� ����� ��������� �������:

��������� ��������� 2 ����� ������ ������ ���. �������� ��� ����� � 2 ����� ���� ��� �������.
        C������� ��������� � ����������� ������� ����� �������� ��� ����� Java Math.
=============================================================================================
'''

int a = 60
int b = 98

println """
---------------------------------------------------------------------------------------------
������� ����� ���������� � ������������� ������ ���� GString:

��������� ���������� � ������� ���������� Groovy a**b: ${a}^${b} = ${a**b}, ��������� ���������� � ������� ����� ���: ${(a**b).class.name}
��������� ���������� � ������� ����� ������ Math: ${a}^${b} = ${Math.pow(a, b)}, ��������� ���������� � ������� ����� ���: ${Math.pow(a, b).class.name}
��������� ���������� � ������� ����� ����������� ��������: ${a}^${b} = ${Math.exp(b*Math.log(a))}, ��������� ���������� � ������� ����� ���: ${Math.exp(b*Math.log(a)).class.name}
---------------------------------------------------------------------------------------------
"""

println '---------------------------------------------------------------------------------------------\n����� � �������������� Closure:\n'

def power = a**b
def printPower = {println "${a}^${b} = ${power}, ��������� ���������� � ������� ����� ���: ${power.class.name}"}

printPower()

power = Math.pow(a, b)
printPower()

power = Math.exp(b*Math.log(a))
printPower()

println '---------------------------------------------------------------------------------------------'

println '---------------------------------------------------------------------------------------------\n����� � �������������� �������� ������ ��������� � Closure:\n'

def printPowerWithOneParam = {pow -> println "${a}^${b} = ${pow}, ��������� ���������� � ������� ����� ���: ${pow.class.name}"}

printPowerWithOneParam(a**b)
printPowerWithOneParam(Math.pow(a, b))
printPowerWithOneParam(Math.exp(b*Math.log(a)))

println '---------------------------------------------------------------------------------------------'

println '---------------------------------------------------------------------------------------------\n����� � �������������� �������� ���������� ���������� � Closure:\n'
def printPowerWithManyParams = {s, pow -> println "${s} ${a}^${b} = ${pow}, ��������� ���������� � ������� ����� ���: ${pow.class.name}"}

printPowerWithManyParams("��������� ���������� � ������� ���������� Groovy a**b:", a**b)
printPowerWithManyParams("��������� ���������� � ������� ����� ������ Math:", Math.pow(a, b))
printPowerWithManyParams("��������� ���������� � ������� ����� ����������� ��������:", Math.exp(b*Math.log(a)))

println '---------------------------------------------------------------------------------------------'

println '---------------------------------------------------------------------------------------------\n����� � �������������� ���������� �������:\n'

def powerGroovy(base, pow) {
    return base**pow
}

def powerMath(base, pow) {
    return Math.pow(base, pow)
}

def powerLog(base, pow) {
    return Math.exp(pow*Math.log(base))
}

power = powerGroovy(a, b)
println "��������� ���������� � ������� ���������� Groovy a**b: ${a}^${b} = ${power}, ��������� ���������� � ������� ����� ���: ${power.class.name}"

power = powerMath(a, b)
println "��������� ���������� � ������� ����� ������ Math: ${a}^${b} = ${power}, ��������� ���������� � ������� ����� ���: ${power.class.name}"

power = powerLog(a, b)
println "��������� ���������� � ������� ����� ����������� ��������: ${a}^${b} = ${power}, ��������� ���������� � ������� ����� ���: ${power.class.name}"

println '---------------------------------------------------------------------------------------------'

println '---------------------------------------------------------------------------------------------\n���� �� ������������ ���������� ���� ��������:\n'

def testClass() {
    given:
        int a = 60
        int b = 98
    when:
        println("��������� ���������� � ������� ���������� Groovy a**b: ${a}^${b} = ${a**b}")
        println("��������� ���������� � ������� ����� ������ Math: ${a}^${b} = ${Math.pow(a, b)}")
        println("��������� ���������� � ������� ����� ����������� ��������: ${a}^${b} = ${Math.exp(b*Math.log(a))}")
    then:
        assert (a**b).class == BigInteger
        assert Math.pow(a, b).class == Double
        assert Math.exp(b*Math.log(a)).class == Double

        assert (a**b) instanceof BigInteger
        assert Math.pow(a, b) instanceof Double
        assert Math.exp(b*Math.log(a)) instanceof Double
}

testClass()

println '---------------------------------------------------------------------------------------------'


println '---------------------------------------------------------------------------------------------\n���� �� ������������ ����������:\n'

def testPower() {
    given:
        int a = 60
        int b = 98
    when:
        println("��������� ���������� � ������� ���������� Groovy a**b: ${a}^${b} = ${a**b}")
        println("��������� ���������� � ������� ����� ������ Math: ${a}^${b} = ${Math.pow(a, b)}")
        println("��������� ���������� � ������� ����� ����������� ��������: ${a}^${b} = ${Math.exp(b*Math.log(a))}")
    then:
        assert a**b == 1814773954166863628046361853216827279269843640202652420952977684359714281881600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        assert Math.pow(a, b) == 1.8147739541668635E174
        assert Math.exp(b*Math.log(a)) == 1.8147739541668317E174
}

testPower()

println '---------------------------------------------------------------------------------------------'