package Car

/**
 * Created by Killgur on 10.11.2015.
 * Класс для создания конкретной модели легкового автомобиля на основе конфигурационного файла
 */

class PassengerCarBuilder extends CarBuilder {
    Car finalCar = null

    //Чтение конфигурации автомобиля
    def makeCar() {
        finalCar = new Car()
        finalCar.with {
            configuration = [
                    model: getStringProperty( configurationFile, 'MODEL' ),
                    maxSpeed: getIntFromStringProperty( configurationFile, 'MAX_SPEED' ),
                    doorsNum: getIntFromStringProperty( configurationFile, 'DOORS' ),
                    wheels: [
                            num: getIntProperty( getListFromStringProperty( configurationFile, 'WHEELS' )[0] ),
                            diameter: getListFromStringProperty( configurationFile, 'WHEELS')[1]
                    ],
                    engine: [
                            type: getEnumProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[0], Car.EngineType.class ),
                            numCylinders: getIntProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[1] ),
                            volume: getIntProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[2] ),
                            placement: getEnumProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[3], Car.PlacementType.class )
                    ],
                    colors: getListFromStringProperty( configurationFile, 'COLOR' ).each { color ->
                        getEnumProperty( color, Car.ColorType.class )
                    }
            ]
            wheelsInstalled = []
            doorsInstalled = []
            engineInstalled = []
            colorsPaint = [:]
        }
    }

    //Установка колес
    def makeWheel( Car.SideType sideType, Car.PlacementType placementType ) {
        finalCar.wheelsInstalled = installWheel( finalCar.wheelsInstalled, sideType, placementType )
    }

    //Установка дверей
    def makeDoor( Car.SideType sideType, Car.PlacementType placementType ) {
        finalCar.doorsInstalled = installDoor( finalCar.doorsInstalled, sideType, placementType )
    }

    //Делаем хэтчбэк
    def makeHatchback( Car.PlacementType placementType ) {
        finalCar.doorsInstalled = installBackDoor( finalCar.doorsInstalled, placementType )
    }

    //Установка двигателя
    def makeEngine( Car.PlacementType placementType ) {
        finalCar.engineInstalled = installEngine( finalCar.engineInstalled, placementType )
    }

    //Покраска
    def makePaint( List colors ) {
        finalCar.colorsPaint = paintAll( colors, finalCar.colorsPaint )
    }
}
