import groovy.transform.TypeChecked

/**
 * Created by Killgur on 24.11.2015.
 * Статические утки
 */

@TypeChecked
// Статическая утка
class StaticDuck {
    String name = ''
    boolean isFlying = false
    List< String > quacks = []
    def quacking

    // Вызываем статические кряканья
    void quack( Closure quack ) {
        StaticQuacks qua = new StaticQuacks()
        qua.quack( quack )
        this.quacks = qua.quacks as List< String >
    }

    String fly( boolean isFluing ) {
        if ( isFlying ) 'flies' else 'walks'
    }

    void methodMissing( String name, arguments ) {
        if ( name in [ 'quacks', 'sings', 'speaks' ] ) {
            quack( this.quacking as Closure )
            println "The STATIC duck ${ this.name.toUpperCase() }" + " ${ fly( this.isFlying ) } and " + "$name: ${ this.quacks.join(' ') }"
        } else {
            println "The STATIC duck ${ this.name.toUpperCase() }" + " was shooted by hunter and can't " + "$name. RIP :("
        }
    }
}

@TypeChecked
// Класс, создающий статичесике кряканья
class StaticQuacks {
    List< String > quacks = []

    void methodMissing( String name, arguments ) {
        if ( name in [ 'quack', 'say', 'shout', 'sing' ] ) {
            arguments.each { it ->
                switch ( it ) {
                    case { it instanceof String || it instanceof Closure }:
                        quacks << it.toString()
                        break
                    case List:
                        List< String > q = it as List< String >
                        quacks << q.join(' ')
                        break
                    case Map:

                        quacks += ( it['quack'] instanceof List ) ? it['quack'].join(' ') : it['quack'].toString()
                        break
                }
            }
        } else {
            quacks << "Listen, dude! I'm only the duck - i can't do this! :)"
        }
    }

    void quack( Closure quacks ) {
        runClosure quacks
    }

    private runClosure( Closure runClosure ) {
        runClosure.delegate = this
        runClosure.resolveStrategy = Closure.DELEGATE_ONLY
        runClosure()
    }
}

@TypeChecked
// Билдер для статической утки
class StaticDuckBuilder {
    void build( StaticDuck duck, Map definition ) {
        duck.with {
            name = definition['name']
            isFlying = definition['isFlying']
            quacking = definition['quacking']
        }
    }
}