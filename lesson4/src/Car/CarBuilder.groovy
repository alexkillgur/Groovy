package Car

/**
 * Created by Killgur on 10.11.2015.
 * Общий класс для конструктора автомобилей
 */

abstract class CarBuilder {
    //Конфигурационный файл
    File configurationFile

    //Чтение конфигурации из файла
    abstract makeCar()
    //Установка колес
    abstract makeWheel( Car.SideType sideType, Car.PlacementType placementType )
    //Установка дверей
    abstract makeDoor( Car.SideType sideType, Car.PlacementType placementType )
    //Хэтчбэк
    abstract makeHatchback( Car.PlacementType placementType )
    //Установка двигателя
    abstract makeEngine( Car.PlacementType placementType )
    //Покраска
    abstract makePaint( List colors )

    //Возвращает строку после описания компонента
    def getStringProperty = { File file, String property ->
        String value
        file.eachLine { line ->
            if ( line =~ /^${property}/ )
                value = line - /${property}:/
        }
        return value.trim()
    }

    //Возвращает целочисленный параметр
    def getIntProperty = { String property ->
        return property as Integer
    }

    //Возвращает перечисляемый параметр
    def getEnumProperty = { String property, enumType ->
        return Enum.valueOf( enumType, property )
    }

    //Возвращает список параметров
    def getListProperty = { String property ->
        return property.tokenize("\n;, ")
    }

    //Возвращает композицию ЧИСЛО << СТРОКА
    def getIntFromStringProperty = getIntProperty << getStringProperty
    //Возвращает композицию СТРОКА >> СПИСОК
    def getListFromStringProperty = getStringProperty >> getListProperty

    //Установка компонента
    def installPart = { String part, List partsList, Car.SideType sideType, Car.PlacementType placementType ->
        sideType == Car.SideType.NONE ? ( partsList += "${placementType}_${part}" ) : ( partsList += "${sideType}_${placementType}_${part}" )
    }

    //Установка двери
    def installDoor = installPart.curry( 'DOOR' )
    //Делает хэтчбэк
    def installBackDoor = installDoor.ncurry( 1, Car.SideType.NONE ) //Zero-based index
    //Установка двигателя
    def installEngine = installPart.curry( 'ENGINE' ).ncurry( 1, Car.SideType.NONE ) //Zero-based index
    //Установка колеса
    def installWheel = installPart.curry( 'WHEEL' )

//    //Алгоритм покраски
//    def paintMap = { List paintList ->
//        Map paintMap = [:]
//        paintList.each { it -> paintMap << [ ( it ):'' ] }
//        return paintMap
//    }

    //Красит заданным цветом заданную часть кузова, по умолчанию весь автомобиль
    def paintPart = { String colorType, Car.PaintType paintType = Car.PaintType.ALL ->
        return [( paintType ):colorType]
    }
    //Красит кузов
    def paintBody = paintPart.rcurry( Car.PaintType.BODY )
    //Красит крышу
    def paintRoof = paintPart.rcurry( Car.PaintType.ROOF )
    //Грунтует
    def paintPrimer = paintPart.curry( Car.ColorType.DEAD_COLOR.toString() ).rcurry( Car.PaintType.PRIMER )

    //Зажимаем болты на чем угодно - мое корявое понимание работы рекурсии с методом trampoline(), честно взятом у mrhaki
    def clampingBolts = { List listOfParts, int numBolts ->
        def bolts
        bolts = { list, counter = 0 ->
            if ( list.size() == 0 ) {
                return counter*numBolts
            } else {
                bolts.trampoline( list.tail(), counter + 1 )
            }
        }.trampoline()
        numBolts = bolts( listOfParts )
        return listOfParts << "BOLTS CLAMPING: $numBolts"
    }

//    //Красит что угодно
//    def paint = { Closure paintSomething, Map paintMap ->
//        return paintMap.add( paintSomething )
//    }

    //Красит весь автомобиль согласно кол-ву цветов по заранее известному алгоритму
    def paintAll = { List colorList, Map colorMap ->
        colorMap << paintPrimer()
        switch ( colorList.size() ) {
            case 1:
                colorMap << paintPart( colorList[0] )
//                paint( paintPart( colorList[0] ), colorMap )
                break
            case 2:
                colorMap << paintBody( colorList[0] )
                colorMap << paintRoof( colorList[1] )
//                paint( paintBody( colorList[0] ), colorMap )
//                paint( paintRoof( colorList[1] ), colorMap )
                break
        }
        return colorMap
    }

    //Возвращает созданный автомобиль
    Car getCar() {
        return finalCar
    }
}