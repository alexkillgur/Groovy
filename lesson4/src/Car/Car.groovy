package Car
import groovy.transform.*

/**
 * Created by Killgur on 10.11.2015.
 * Класс для описания автомобилей
 */

//@Canonical
//@ToString ( includeNames = true, includeFields = true)
@ToString ( includeNames = true)
class Car {
    //Тип стороны (левая, правая)
    enum SideType {
        LEFT, RIGHT, NONE
    }

    //Тип размещения (перед, назад)
    enum PlacementType {
        FRONT, BACK
    }

    //Цвета
    enum ColorType {
        RED, BLUE, WHITE, METALLIC, BEIGE, BLACK, DEAD_COLOR
    }

    //Виды покраски
    enum PaintType {
        PRIMER, ALL, ROOF, BODY
    }

    //Тип двигателя
    enum EngineType {
        INJECTOR, MONOINJECTOR, CARBURETTOR, DIESEL
    }

//    public Map configuration
    Map configuration
    List doorsInstalled //Массив установленных дверей
    List wheelsInstalled //Массив установленных колес
    List engineInstalled //Массив установленного двигателя
    Map colorsPaint //Карта покраски
}
