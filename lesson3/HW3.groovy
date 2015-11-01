/**
 * Created by Killgur on 31.10.2015.
 */
import groovy.transform.ToString

//Виды счетчиков
enum CounterType {
    GAS, ELETRO_1_ZONE, ELETRO_2_ZONE, ELETRO_3_ZONE, COLD_WATER, HOT_WATER_1_TARIFF, HOT_WATER_4_TARIFF, HEAT
}

//Виды потребителей электроэнергии по счетчикам
enum ElectroConsumerType {
    TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, TYPE_7
}

//Виды потребителей газа по счетчикам
enum GasConsumerType {
    TYPE_1, TYPE_2
}

//Виды потребителей газа по норме
enum GasNormType {
    NORM_1, NORM_2, NORM_3, NORM_4, NORM_5
}

//Виды потребителей горячей воды по счетчикам
enum HotWaterConsumerType {
    TYPE_1, TYPE_2
}

//Виды потребителей горячей воды по норме
enum HotWaterNormType {
    NORM_1, NORM_2
}

//Виды потребителей холодной воды по норме
enum ColdWaterNormType {
    NORM_1, NORM_2
}

//Класс, содержащий адрес плательщика/квартиры/организации
@ToString ( includeNames = true, includeFields = true )
class Address {
    String index
    String country
    String rigion
    String city
    String street
    int numHouse
    int numFlat
    int floor

    String getFullAdress () {
//        [ index, country, region, city, street, numHouse, numFlat ].join( ', ' )
//        println { "Индекс: $index, Страна: $country, Область: $region, Город: $city, Улица: $street, Дом: $numHouse, Квартира: $numFlat" }
    }
}

//Общий интерфейс счетчика
interface CounterIF {
    //Установка тарифной сетки и используемых лимитов, если есть
    def setTariff()
    //Интерфейс для подсчета стоимости услуг. За алгоритм подсчета по многотарифным счетчикам утверждать не берусь, т.к. не сталкивался :)
    def getAmount()
}

//Трейт для субсидии
trait SubsidyT {
    double subsidy

    String printSubsidy() {
        println "Начислена субсидия в размере $subsidy"
    }
}

//Трейт для однотарифного электростчечика
trait OneElectroZoneT {
    //Возврат значения стоимости услуги
    def getAmount() {
        setTariff()

        used_1 = []
        usedElectro_1 = ind_1_now - ind_1_before
        fillUsed( usedElectro_1, used_1 )

        return cycleOnUsed( used_1, zoneElFactor[0] )
    }
}

//Трейт для двухтарифного электростчечика
trait TwoElectroZoneT implements OneElectroZoneT {
    //Возврат значения стоимости услуги
    def getAmount() {
        def sum = super.getAmount()

        used_2 = []
        usedElectro_2 = ind_2_now - ind_2_before
        fillUsed( usedElectro_2, used_2 )

        return sum + cycleOnUsed( used_2, zoneElFactor[1] )
    }
}

//Трейт для трехтарифного электростчечика
trait ThreeElectroZoneT implements TwoElectroZoneT {
    //Возврат значения стоимости услуги
    def getAmount() {
        def sum = super.getAmount()

        used_3 = []
        usedElectro_3 = ind_3_now - ind_3_before
        fillUsed( usedElectro_3, used_3 )

        return sum + cycleOnUsed( used_3, zoneElFactor[2] )
    }
}

//Класс для общего описания счетчика
@ToString ( includeNames = true, includeFields = true )
abstract class Counter {
    CounterType type //Тип счетчика
    String model //Модель счетчика
    int number //Номер счетчика
    def tariff = [] //Сетка тарифов
    protected double ind_before //Предыдущие показания счетчика
    protected double ind_now //Текущие показания счетчика
}

//Электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
abstract class ElectroCounter extends Counter implements CounterIF {
    double[] limitation = [] //Сетка лимитов

    ElectroConsumerType electroConsumerType //Тип электропотребителя
    boolean isCity //Квартира находится в городе или селе

    //Задание тарифной сетки и лимитов в зависимости от категории потребителей электроэнергии
    def setTariff() {
        switch ( electroConsumerType ) {
            case ElectroConsumerType.TYPE_1:
                limitation = [ 100, 600 ]
                tariff = [ 0.456, 0.789, 0.147 ]
                break
            case ElectroConsumerType.TYPE_2:
                limitation = [ 150, 600 ]
                tariff = [ 0.456, 0.789, 0.147 ]
                break
            case ElectroConsumerType.TYPE_3:
                // До 09.2015 другой тариф
                if ( Bill.date[Calendar.YEAR] == 2015 && Bill.date[Calendar.MONTH] <= 8) {
                    limitation = isCity ? [ 100, 600 ] : [ 150, 600 ]
                    tariff = [ 0.456, 0.789, 0.147 ]
                }
                else {
                    limitation = [ 3600 ]
                    tariff = [ 0.456, 0.1479 ]
                }
                break
            case ElectroConsumerType.TYPE_4:
                // До 09.2015 другой тариф
                if ( Bill.date[Calendar.YEAR] == 2015 && Bill.date[Calendar.MONTH] <= 8) {
                    limitation = isCity ? [ 100, 600 ] : [ 150, 600 ]
                    tariff = [ 0.456, 0.789, 0.1479 ]
                }
                else {
                    limitation = [ 3600 ]
                    tariff = [ 0.456, 0.1479 ]
                }
                break
            case ElectroConsumerType.TYPE_5:
                limitation = [ 0 ]
                tariff = [ 0.456 ]
                break
            case ElectroConsumerType.TYPE_6:
                limitation = [ 0 ]
                tariff = [ 0.789 ]
                break
            case ElectroConsumerType.TYPE_7:
                limitation = [ 0 ]
                tariff = [ 0.456 ]
                break
        }
    }

    //Заполнение массива использованных кВт согласно тарифной сетке и лимитам (сколько было использовано в каждом отрезке лимитов)
    def fillUsed( usedElectro, used ) {
        switch ( limitation.size() ) {
            case 1:
                if ( usedElectro <= limitation[0] ) {
                    used << usedElectro
                }
                else {
                    used << limitation[0]
                    usedElectro -= limitation[0]
                    used << usedElectro
                }
                break
            case 2:
                if ( usedElectro <= limitation[0] ) {
                    used << usedElectro
                }
                else if ( usedElectro > limitation[0] && usedElectro <= limitation[1] ) {
                    used << limitation[0]
                    usedElectro -= limitation[0]
                    used << usedElectro
                }
                else {
                    used << limitation[0]
                    usedElectro -= limitation[0]
                    used << ( limitation[1] - limitation[0] )
                    usedElectro -= limitation[1] - limitation[0]
                    used << usedElectro
                }
                break
        }
    }

    //Обход по сетке использованных кВт и возвращение стоимости в зависимости от лимитов и коэффициента
    def cycleOnUsed( used, factor ) {
        def sum = 0
        used.eachWithIndex { it, i ->
            sum += factor*it*tariff[i]
        }
        return sum
    }

    //Возврат значения стоимости услуги по умолчанию
    def getAmount() {
        println 'Показания электрического счетчика не предоставлены'
        return 0
    }
}

//Однотарифный электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class OneZoneElCounter extends ElectroCounter {
    def used_1 = [] //Массив использованных кВт согласно лимитам для подсчета стоимости согласно тарифам
    double ind_1_before //Предыдущие показания счетчика по 1-й зоне
    double ind_1_now //Текущие показания счетчика по 1-й зоне
    double usedElectro_1 //Всего кВт использовано
    double[] zoneElFactor = [1] //Коэффициенты для однотарифного счетчика
}

//Двухтарифный электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class TwoZoneElCounter extends OneZoneElCounter {
    def used_2 = [] //Теоретически можно обойтись одним массивом в суперклассе. Пока оставил
    double ind_2_before //Предыдущие показания счетчика по 2-й зоне
    double ind_2_now //Текущие показания счетчика по 2-й зоне
    double usedElectro_2 //Теоретически можно обойтись одним значением в суперклассе. Пока оставил
    double[] zoneElFactor = [1, 0.7] //Коэффициенты для двухтарифного счетчика
}

//Трехтарифный электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ThreeZoneElCounter extends TwoZoneElCounter {
    def used_3 = [] //Теоретически можно обойтись одним массивом в суперклассе. Пока оставил
    double ind_3_before //Предыдущие показания счетчика по 3-й зоне
    double ind_3_now //Текущие показания счетчика по 3-й зоне
    double usedElectro_3 //Теоретически можно обойтись одним значением в суперклассе. Пока оставил
    double[] zoneElFactor = [1.5, 1, 0.7] //Коэффициенты для трехтарифного счетчика
}

//Газовый счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class GasCounter extends Counter implements CounterIF {
    protected double[] limitation = [] //Сетка лимитов
    double usedGas //Всего м3 использовано
    def used = [] //Массив использованных м3 согласно лимитам для подсчета стоимости согласно тарифам

    GasConsumerType gasConsumerType //Тип требителя газа

    //Задание тарифной сетки и лимитов в зависимости от категории потребителей газа
    def setTariff() {
        switch ( gasConsumerType ) {
            case GasConsumerType.TYPE_1:
                limitation = [ 0 ]
                tariff = [ 7.188 ]
                used << usedGas
                break
            case GasConsumerType.TYPE_2:
                // Тариф с мая по сентябрь
                if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
                    limitation = [ 0 ]
                    tariff = [ 7.188 ]
                    used << usedGas
                }
                // Тариф с октября по апрель
                else {
                    limitation = [ 200 ]
                    tariff = [ 3.6, 7.188 ]
                    used << limitation[0]
                    usedGas -= limitation[0]
                    used << usedGas
                }
                break
        }
    }

    //Возврат значения стоимости услуги
    def getAmount() {
        used = []
        usedGas = ind_now - ind_before
        setTariff()

        def sum = 0
        used.eachWithIndex { it, i ->
            sum += it*tariff[i]
        }
        return sum
    }
}

//Счетчик холодной воды
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ColdWaterCounter extends Counter implements CounterIF {
    double usedColdWater //Всего м3 использовано
    boolean isWaterOut //Водоотведение

    //Задание тарифной сетки
    def setTariff() {
        tariff = isWaterOut ? [ 4.092 ] : [ 4.284 ]
    }

    //Возврат значения стоимости услуги
    def getAmount() {
        usedColdWater = ind_now - ind_before
        setTariff()

        return usedColdWater*tariff[0]
    }
}

//Счетчик горячей воды
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HotWaterCounter extends Counter implements CounterIF {
    double usedHotWater //Всего м3 использовано
    def used = [] //Массив использованных м3 согласно показаниям многотарифного счетчика
    double[] zoneHotWaterFactor = [1, 0.9, 0.7, 0 ] //Коэффициенты для четырехтарифного счетчика

    HotWaterConsumerType hotWaterConsumerType //Тип требителя горячей воды

    //Задание тарифной сетки
    def setTariff() {
        switch ( hotWaterConsumerType ) {
            case HotWaterConsumerType.TYPE_1:
                tariff = [ 40.92 ]
                break
            case HotWaterConsumerType.TYPE_2:
                tariff = [ 38.02 ]
                break
        }
        if ( type == CounterType.HOT_WATER_4_TARIFF ) tariff << 5.676
    }

    //Возврат значения стоимости услуги
    def getAmount() {
        setTariff()
        switch ( type ) {
        //Обычный счетчик
            case CounterType.HOT_WATER_1_TARIFF:
                usedHotWater = ind_now - ind_before
                return usedHotWater*tariff[0]
        //Многотарифный счетчик
            case CounterType.HOT_WATER_4_TARIFF:
                def sum = 0
                used.eachWithIndex { it, i ->
                    sum += it*tariff[0]*zoneHotWaterFactor[i]
                }
                return sum += used[3]*tariff[1]
        }

    }
}

//Счетчик тепла
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HeatCounter extends Counter implements CounterIF {
    double usedHeat //Всего Гкал использовано

    //Задание тарифной сетки
    def setTariff() {
        tariff = [ 642.09 ]
    }

    //Возврат значения стоимости услуги
    def getAmount() {
        usedHeat = ind_now - ind_before
        setTariff()

        return usedHeat*tariff[0]
    }
}

//Обобщенный класс норм потребления
@ToString ( includeNames = true, includeFields = true )
abstract class Norm {
    protected double norm //Норма
    protected double tariff //Тариф

    //Возврат значения стоимости услуги
    def getAmount() {
        setTariff()
        return norm*tariff
    }
}

//Норма газа
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class GasNorm extends Norm implements CounterIF {
    int numRegistration //Количество прописанных жителей
    int numAnimal //Количество животных
    double heatArea //Отапливаемая площадь

    GasNormType gasNormType //Тип нормы

    //Задание тарифной сетки (нормы)
    def setTariff() {
        tariff = 7.188
        switch ( gasNormType ) {
            case GasNormType.NORM_1:
                norm = 3*numRegistration
                break
            case GasNormType.NORM_2:
                norm = 4.5*numRegistration
                break
            case GasNormType.NORM_3:
                norm = 9*numRegistration
                break
            case GasNormType.NORM_4:
                // Тариф с мая по сентябрь
                if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
                    norm = ( numRegistration + numAnimal <= 3 ) ? 70 : ( 70 + 11*( numRegistration + numAnimal - 3 ) )
                }
                // Тариф с октября по апрель
                else {
                    norm = ( heatArea <= 20 ) ? 20 : ( 20 + 11*( heatArea - 20 ) )
                }
                break
            case GasNormType.NORM_5:
                norm = 11*heatArea
                break
        }
    }
}

//Норма горячей воды
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HotWaterNorm extends Norm implements CounterIF {
    int numRegistration //Количество прописанных жителей

    HotWaterNormType hotWaterNormType //Тип нормы

    //Задание тарифной сетки (нормы)
    def setTariff() {
        norm = 3*numRegistration
        switch ( hotWaterNormType ) {
            case HotWaterNormType.NORM_1:
                tariff = 40.92
                break
            case HotWaterNormType.NORM_2:
                tariff = 38.02
                break
        }
    }
}

//Норма холодной воды
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ColdWaterNorm extends Norm implements CounterIF {
    int numRegistration //Количество прописанных жителей
    boolean isWaterOut //Водоотведение

    ColdWaterNormType coldWaterNormType //Тип нормы

    //Задание тарифной сетки (нормы)
    def setTariff() {
        tariff = isWaterOut ? 4.092 : 4.284
        switch ( coldWaterNormType ) {
            case ColdWaterNormType.NORM_1:
                norm = isWaterOut ? 9*numRegistration : 5.5*numRegistration
                break
            case ColdWaterNormType.NORM_2:
                norm = isWaterOut ? 11.1*numRegistration : 8.1*numRegistration
                break
        }
    }
}

//Норма отопления
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HeatNorm extends Norm implements CounterIF {
    double heatArea //Отапливаемая площадь

    //Задание тарифной сетки (нормы)
    def setTariff() {
        norm = heatArea
        // Тариф с мая по сентябрь
        if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
            tariff = 0
        }
        // Тариф с октября по апрель
        else {
            tariff = 16.42
        }
    }
}

//СУБ
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class SubNorm extends Norm implements CounterIF {
    double allArea //Общая площадь

    //Задание тарифной сетки (нормы)
    def setTariff() {
        norm = allArea
        tariff = 2.459
    }
}

//Вывоз мусора
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class TrashNorm extends Norm implements CounterIF {
    int numRegistration //Количество прописанных жителей

    //Задание тарифной сетки (нормы)
    def setTariff() {
        norm = numRegistration
        tariff = 7.86
    }
}

//Класс для описания квартиры, за которую платит плательщик
@ToString ( includeNames = true, includeFields = true )
class Flat {
    double allArea //Общая площадь
    double heatArea //Отапливаемая площадь
    int numRegistration //Количество прописанных
    int numAnimal //Количество животных
    boolean isCity //Квартира находится в городе или селе
    ElectroConsumerType electroConsumerType //Тип электропотребителя по счетчикам
    GasConsumerType gasConsumerType //Тип потребителя газа по счетчикам
    GasNormType gasNormType //Тип потребителя газа по норме
    HotWaterConsumerType hotWaterConsumerType //Тип потребителя горячей воды по счетчикам
    HotWaterNormType hotWaterNormType //Тип потребителя горячей воды по норме
    ColdWaterNormType coldWaterNormType //Тип потребителя холодной воды по норме
    boolean isGasCounter //Есть ли газовый счетчик
    boolean isHotWaterCounter //Есть ли счетчик горячей воды
    boolean isColdWaterCounter //Есть ли счетчик холодной воды
    boolean isHeatCounter //Есть ли счетчик тепла
}

//Класс для описания организации, которая выставила счет
@ToString ( includeNames = true, includeFields = true )
class Organisation {

}

//Класс для общего описания плательщика
@ToString ( includeNames = true, includeFields = true )
class Payer {

}

//Класс для общего описания платежки
@ToString
abstract class Bill {
    static date
}

//----------------------НАЧАЛО----------------------

//Задаем дату для платежек
Bill.date = new Date()
Bill.date = Bill.date.copyWith(
        year: 2015,
        month: Calendar.OCTOBER,
        date: 15 )

//Создаем квартиру
def flat = new Flat()
flat.with {
    allArea = 45.1
    heatArea = 44.1
    numRegistration = 2
    numAnimal = 1
    electroConsumerType = ElectroConsumerType.TYPE_1
    gasConsumerType = GasConsumerType.TYPE_1
    gasNormType = GasNormType.NORM_1
    hotWaterConsumerType = HotWaterConsumerType.TYPE_2
    hotWaterNormType = HotWaterNormType.NORM_2
    coldWaterNormType = ColdWaterNormType.NORM_1
    isGasCounter = false
    isHotWaterCounter = false
    isColdWaterCounter = false
    isCity = true
}

//---------------------------------------------------
//Секция для игры с трейтами и наследованием на примере счетчиков
def counterAmount

def gasNorm = new GasNorm()
gasNorm.with {
    numRegistration = flat.numRegistration
    numAnimal = flat.numAnimal
    heatArea = flat.heatArea
    gasNormType = flat.gasNormType
}

counterAmount = gasNorm.getAmount()
println "Газ по норме - $counterAmount"

def hotWaterNorm = new HotWaterNorm()
hotWaterNorm.with {
    numRegistration = flat.numRegistration
    hotWaterNormType = flat.hotWaterNormType
}

counterAmount = hotWaterNorm.getAmount()
println "Горячая вода по норме - $counterAmount"

def coldWaterNorm = new ColdWaterNorm()
coldWaterNorm.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = false
}

counterAmount = coldWaterNorm.getAmount()
println "Водоснабжение по норме - $counterAmount"

coldWaterNorm = new ColdWaterNorm()
coldWaterNorm.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = true
}

counterAmount = coldWaterNorm.getAmount()
println "Водоотведение по норме - $counterAmount"

heatNorm = new HeatNorm()
heatNorm.with {
    heatArea = flat.heatArea
}

counterAmount = heatNorm.getAmount()
println "Отопление по норме - $counterAmount"

subNorm = new SubNorm()
subNorm.with {
    allArea = flat.allArea
}

counterAmount = subNorm.getAmount()
println "СУБ - $counterAmount"

trashNorm = new TrashNorm()
trashNorm.with {
    numRegistration = flat.numRegistration
}

counterAmount = trashNorm.getAmount()
println "Вывоз мусора - $counterAmount"

def heatCounter = new HeatCounter()
heatCounter.with {
    type = CounterType.HEAT
    model = 'GROSS ETR-UA'
    number = 1006
    ind_before = 24.08
    ind_now = 24.56
}

counterAmount = heatCounter.getAmount()
println "Отопление по счетчику - $counterAmount"

def hotWaterCounter = new HotWaterCounter()
hotWaterCounter.with {
    type = CounterType.HOT_WATER_4_TARIFF
    model = 'JS 1.5 POWOGAS'
    number = 11024
    switch ( type ) {
        case CounterType.HOT_WATER_1_TARIFF:
            ind_before = 453.8
            ind_now = 465.4
            break
        case CounterType.HOT_WATER_4_TARIFF:
            used = [ 2.5, 4.3, 1.5, 3.3 ]
            break
    }
    hotWaterConsumerType = flat.hotWaterConsumerType
}

counterAmount = hotWaterCounter.getAmount()
println "Горячая вода по счетчику - $counterAmount"

def coldWaterCounter = new ColdWaterCounter()
coldWaterCounter.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = false
}

counterAmount = coldWaterCounter.getAmount()
println "Водоснабжение по счетчику - $counterAmount"

coldWaterCounter = new ColdWaterCounter()
coldWaterCounter.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = true
}

counterAmount = coldWaterCounter.getAmount()
println "Водотведение по счетчику - $counterAmount"

def gasCounter = new GasCounter()
gasCounter.with {
    type = CounterType.GAS
    model = 'ВИЗАР G4'
    number = 13400
    ind_before = 16826
    ind_now = 17403
    gasConsumerType = flat.gasConsumerType
}

counterAmount = gasCounter.getAmount()
println "Газ по счетчику $counterAmount"

def elCounter = new OneZoneElCounter() as OneElectroZoneT
elCounter.with {
    type = CounterType.ELETRO_1_ZONE
    model = 'СЭТ1-1-1-Ш-С2-У'
    number = 18100
    ind_1_before = 18660
    ind_1_now = 18825
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

def elCounter_2 = new TwoZoneElCounter() as TwoElectroZoneT
elCounter_2.with {
    type = CounterType.ELETRO_2_ZONE
    model = 'СЭТ 1-4М'
    number = 19100
    ind_1_before = 18660
    ind_1_now = 18770
    ind_2_before = 18160
    ind_2_now = 18215
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

def elCounter_3 = new ThreeZoneElCounter().withTraits ThreeElectroZoneT, SubsidyT
elCounter_3.with {
    type = CounterType.ELETRO_3_ZONE
    model = 'СЭТ 1-4М'
    number = 19100
    ind_1_before = 18660
    ind_1_now = 18740
    ind_2_before = 18160
    ind_2_now = 18200
    ind_3_before = 18160
    ind_3_now = 18205
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity

    subsidy = 21.37
}

def elCounter_4 = new OneZoneElCounter()
elCounter.with {
    type = CounterType.ELETRO_1_ZONE
    model = 'СЭТ1-1-1-Ш-С2-У'
    number = 18100
    ind_1_before = 18660
    ind_1_now = 18825
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

counterAmount = elCounter.getAmount()
println "1-зонный электросчетчик с трейтом подсчета стоимости - $counterAmount"

counterAmount = elCounter_2.getAmount()
println "2-зонный электросчетчик с трейтом подсчета стоимости - $counterAmount"

counterAmount = elCounter_3.getAmount()
println "3-зонный электросчетчик с трейтом подсчета стоимости - $counterAmount"

counterAmount = elCounter_3.getAmount() - elCounter_3.subsidy
println "3-зонный электросчетчик с трейтами подсчета стоимости и субсидией - $counterAmount"
elCounter_3.printSubsidy()

counterAmount = elCounter_4.getAmount()
println "1-зонный дефолтный электросчетчик - $counterAmount"