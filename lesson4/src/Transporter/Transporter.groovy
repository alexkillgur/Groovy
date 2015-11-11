package Transporter
import Car.*

/**
 * Created by Killgur on 10.11.2015.
 * Класс сборочного конвейера для чего угодно
 */

class Transporter {
    //Файл операций для сборки чего угодно
    File operationsFile
    //Конфигурация, загруженная из файла в соответствующий конструктор соответствующего класса
    Map mapConfiguration

    //Создание легкового автомобиля
    Car makePassengerCar( PassengerCarBuilder builder ) {
        builder.makeCar()

        mapConfiguration = builder.car.configuration

        operationsFile.eachLine { line ->
            switch ( line ) {
            //Двери
                case ( 'DOORS' ):
                    builder.&makeDoor( Car.SideType.LEFT, Car.PlacementType.FRONT )
                    builder.&makeDoor( Car.SideType.RIGHT, Car.PlacementType.FRONT )

                    if ( mapConfiguration.doorsNum > 2 && mapConfiguration.doorsNum != 3 ) {
                        builder.&makeDoor( Car.SideType.LEFT, Car.PlacementType.BACK )
                        builder.&makeDoor( Car.SideType.RIGHT, Car.PlacementType.BACK )
                    }

                    //Хэтчбэк
                    if ( mapConfiguration.doorsNum == 3 || mapConfiguration.doorsNum == 5 ) {
                        builder.&makeHatchback( Car.PlacementType.BACK )
                    }
                    break
            //Колеса
                case ( 'WHEELS' ):
//                    builder.&makeWheel( Car.SideType.LEFT, Car.PlacementType.FRONT )
//                    builder.&makeWheel( Car.SideType.RIGHT, Car.PlacementType.FRONT )
//                    builder.&makeWheel( Car.SideType.LEFT, Car.PlacementType.BACK )
//                    builder.&makeWheel( Car.SideType.RIGHT, Car.PlacementType.BACK )
                    builder.makeWheels()
                    break
            //Двигатель
                case ( 'ENGINE' ):
                    builder.&makeEngine( mapConfiguration.engine.placement )
                    break
            //Покраска
                case ( 'COLOR' ):
                    builder.&makePaint( mapConfiguration.colors )
                    break
            }
        }
        //Возвращаем автомобиль
        return builder.car
    }
    //Здесь могут быть методы для создания чего угодно
}
