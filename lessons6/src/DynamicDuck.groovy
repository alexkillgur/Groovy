/**
 * Created by Killgur on 23.11.2015.
 * ������������ ����
 * ���� ����� � mrhaki ����� - http://mrhaki.blogspot.com/2011/11/groovy-goodness-create-simple-builders.html
 */

// ������������ ����
class DynamicDuck {
    def name = ''
    def quacks = []
    def isFlying = false

    def fly = { -> if ( isFlying ) 'flies' else 'walks' }

    def methodMissing( String name, arguments ) {
        if ( name in [ 'quacks', 'sings', 'speaks' ] ) {
            println "The DYNAMIC duck ${ this.name.toUpperCase() }" + " ${ fly } and " + "$name: ${ quacks.join(' ') }"
        } else {
            println "The DYNAMIC duck ${ this.name.toUpperCase() }" + " was shooted by hunter and can't " + "$name. RIP :("
        }
    }
}

// ����� ��� �������� �� ���������
// � ������ ������� ���������� �� ���������� �������� ���� �������� �������� "message", �� ����� ������� ��� ������
class DynamicQuacks {
    def quacks

    String toString() {
        def message = 'QUACK - '
        switch ( quacks ) {
            case { it instanceof String || it instanceof Closure }:
                message + quacks
                break
            case List:
                message + quacks.join(' ')
                break
            case Map:
                quacks = ( quacks['quack'] instanceof List ) ? message + quacks['quack'].join(' ') : message + quacks['quack']
                break
        }
    }
}

// ������ ��� ������������ ����
class DynamicDuckBuilder {
    DynamicDuck dynamicDuck

    // ����������, "�����������"
    DynamicDuck build( definition ) {
        dynamicDuck = new DynamicDuck()
        runClosure definition
        dynamicDuck
    }

    // ���
    void name( duckName ) {
        dynamicDuck.name = duckName
    }

    // ����� �� ����
    void isFlying( isFlying ) {
        dynamicDuck.isFlying = isFlying
    }

    // ���������� ������� �������� ��������
    void quacking( quacks ) {
        runClosure quacks
    }

    // ������� �� ���������
    void quack( quack ) {
        dynamicDuck.quacks << new DynamicQuacks( quacks: quack )
    }

    // �������, ����, ������ ������ ������� ���������
    def methodMissing( String name, arguments ) {
        if ( name in [ 'say', 'shout', 'sing' ] ) {
            arguments.each { it ->
                switch ( it ) {
                    case { it instanceof String || it instanceof Closure }:
                        dynamicDuck.quacks << it
                        break
                    case List:
                        dynamicDuck.quacks << it.join(' ')
                        break
                    case Map:
                        dynamicDuck.quacks += ( it['quack'] instanceof List ) ? it['quack'].join(' ') : it['quack']
                        break
                }
            }
        } else {
            dynamicDuck.quacks << "Listen, dude! I'm only the duck - i can't do this! :)"
        }
    }

    // ������� ����������� � ����������
    private runClosure( runClosure ) {
        runClosure.delegate = this
        runClosure.resolveStrategy = Closure.DELEGATE_ONLY
        runClosure()
    }
}