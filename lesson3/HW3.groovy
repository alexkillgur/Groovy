/**
 * Created by Killgur on 31.10.2015
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

//Трейт, содержащий адрес плательщика/квартиры/организации
trait AddressT {
    String index
    String country
    String region
    String city
    String street
    String numHouse
    String numFlat

    String getFullAddress() {
        def strAddr = "Индекс: $index, Страна: $country, Область: $region, г. $city, ул. $street, д. $numHouse"
        if ( numFlat )
            strAddr += ", кв. $numFlat"
        return strAddr
    }

    String getShortAddress() {
        def strAddr = "$index, г. $city, ул. $street, д. $numHouse"
        if ( numFlat )
            strAddr += ", кв. $numFlat"
        return strAddr
    }
}

//Трейт, содержащий банковские реквизиты
trait BankT {
    String nameBank
    String checkingAccount
    String MFO
    String ZKPO

    String getFullRequisite() {
        "Наименование: $nameBank, р/р: $checkingAccount, МФО: $MFO, ЕДРПОУ (ЗКПО): $ZKPO"
    }
}

//Интерфейс для печати с заглушкой в обход трейтов
trait PrintClassT {
    def getPrintName() {
        this
    }
}

//Общий интерфейс счетчика
interface CounterIF {
    //Установка тарифной сетки и используемых лимитов, если есть
    def setTariff()
    //Интерфейс для подсчета стоимости услуг. За алгоритм подсчета по многотарифным счетчикам утверждать не берусь, т.к. не сталкивался :)
    def getAmount()
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
    def ind_before //Предыдущие показания счетчика
    def ind_now //Текущие показания счетчика
}

//Электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
abstract class ElectroCounter extends Counter implements CounterIF, PrintClassT {
    def limitation = [] //Сетка лимитов

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
    def ind_1_before //Предыдущие показания счетчика по 1-й зоне
    def ind_1_now //Текущие показания счетчика по 1-й зоне
    def usedElectro_1 //Всего кВт использовано
    def zoneElFactor = [1] //Коэффициенты для однотарифного счетчика
}

//Двухтарифный электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class TwoZoneElCounter extends OneZoneElCounter {
    def used_2 = [] //Теоретически можно обойтись одним массивом в суперклассе. Пока оставил
    def ind_2_before //Предыдущие показания счетчика по 2-й зоне
    def ind_2_now //Текущие показания счетчика по 2-й зоне
    def usedElectro_2 //Теоретически можно обойтись одним значением в суперклассе. Пока оставил
    def zoneElFactor = [1, 0.7] //Коэффициенты для двухтарифного счетчика
}

//Трехтарифный электрический счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ThreeZoneElCounter extends TwoZoneElCounter {
    def used_3 = [] //Теоретически можно обойтись одним массивом в суперклассе. Пока оставил
    def ind_3_before //Предыдущие показания счетчика по 3-й зоне
    def ind_3_now //Текущие показания счетчика по 3-й зоне
    def usedElectro_3 //Теоретически можно обойтись одним значением в суперклассе. Пока оставил
    def zoneElFactor = [1.5, 1, 0.7] //Коэффициенты для трехтарифного счетчика
}

//Газовый счетчик
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class GasCounter extends Counter implements CounterIF {
    def limitation = [] //Сетка лимитов
    def usedGas //Всего м3 использовано
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
    def usedColdWater //Всего м3 использовано
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
    def usedHotWater //Всего м3 использовано
    def used = [] //Массив использованных м3 согласно показаниям многотарифного счетчика
    def zoneHotWaterFactor = [1, 0.9, 0.7, 0 ] //Коэффициенты для четырехтарифного счетчика

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
    def usedHeat //Всего Гкал использовано

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
    def norm //Норма
    def tariff //Тариф

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
    def heatArea //Отапливаемая площадь

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
    def heatArea //Отапливаемая площадь

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
    def allArea //Общая площадь

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
class Flat implements PrintClassT {
    def allArea //Общая площадь
    def heatArea //Отапливаемая площадь
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
class Organisation implements PrintClassT {
    String nameOrganisation

    String getFullName() {
        "Наименование: $nameOrganisation"
    }
}

//Простенький интерфейс поиграться с цепочкой вызовов. Честно стащил кусок кода у mrhaki и малость проапгрейдил :)
interface TransformerIF {
    String transform(String[] str)
}

//Возвращает строку из элементов массива строк
trait DefaultTransformerT implements TransformerIF {
    String transform(String[] str) {
        def sb = new StringBuilder()
        str.each { it -> sb.append "$it " }
        return sb
    }
}

//Переводит все элементы массива строк в верхний регистр
trait UpperT implements TransformerIF {
    String transform(String[] str) {
        str.eachWithIndex { it, i -> str[i] = it.toUpperCase() }
        super.transform( str )
    }
}

//Класс для общего описания плательщика
@ToString ( includeNames = true, includeFields = true )
class Payer implements PrintClassT, DefaultTransformerT, UpperT {
    String firstName
    String lastName
    String patronymic
    String personalAccount

    String getFullName() {
        "Имя: $firstName, Фамилия: $lastName, Отчество: $patronymic, л/с: $personalAccount"
    }

    String getShortName() {
        transform( firstName, lastName, patronymic )
    }
}

//
interface BillingIF {
    def getMonth()
    def getCounted()
    def getToPay()
    boolean isValidate()
    def getBilling()
}

//Класс для общего описания платежки
@ToString ( includeNames = true, includeFields = true )
class Bill implements BillingIF, PrintClassT {
    static date //Месяц, за который выставлен счет

    String nameBill
    def debt //Долг на начало месяца
    def recalculation //Перерасчет
    def payed //Оплачено на начало месяца
    def subsidy //Насчитано субсидии
    def mustBePayed //Указано к оплате
    boolean isCounter //Есть ли счетчики

    def client //Особа, которая платит
    def organisation //Организация, выставившая счет
    def flatToPay //Квартира, за которую платят
    def counterOrNorm //Либо считаем по норме, либо по счетчику

    //Выдает месяц оплаты
    def getMonth() {
        switch ( date[Calendar.MONTH] ) {
            case 0: return 'ЯНВАРЬ'
            case 1: return 'ФЕВРАЛЬ'
            case 2: return 'МАРТ'
            case 3: return 'АПРЕЛЬ'
            case 4: return 'МАЙ'
            case 5: return 'ИЮНЬ'
            case 6: return 'ИЮЛЬ'
            case 7: return 'АВГУСТ'
            case 8: return 'СЕНТЯБРЬ'
            case 9: return 'ОКТЯБРЬ'
            case 10: return 'НОЯБРЬ'
            case 11: return 'ДЕКАБРЬ'
        }
    }

    //Рассчитывает оплату по счету или норме
    def getCounted() {
        ( counterOrNorm.amount ).setScale( 2, BigDecimal.ROUND_HALF_UP )
    }

    //Высчитывает к оплате
    def getToPay() {
        ( debt + counted + recalculation - payed - subsidy ).setScale( 2, BigDecimal.ROUND_HALF_UP )
    }

    //Возвращает равенство высчитанного к оплате и показаний счетчика (нормы)
    boolean isValidate() {
        mustBePayed == toPay
    }

    //Печатает платежку
    def getBilling() {
        def printBilling =
                """
        -----------------------------------------------------${nameBill}---------------------------------------------------------
        Плательщик: $client.shortName
        Адрес плательщика: $client.shortAddress
        Адрес квартиры: $flatToPay.shortAddress
        Организация: $organisation.fullName
        Адрес организации: $organisation.shortAddress
        Реквизиты: $organisation.fullRequisite
        Счет за: $month
        -------------------------------------------------------------------------------------------------------------------------------------
        Долг: $debt | Перерасчет: $recalculation | Оплата: $payed | Субсидия: $subsidy | Выставлено организацией к оплате: $mustBePayed |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        if ( isCounter ) {
            printBilling +=
                    """                                                                  Насчитано к оплате по счетчику: $counted |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        else {
            printBilling +=
                    """                                                                     Насчитано к оплате по норме: $counted |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        printBilling +=
                """                                 Посчитано программой к оплате с учетом показаний счетчика (нормы): $toPay |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        if ( !validate ) {
            printBilling +=
                    """          ВНИМАНИЕ! ВЫСТАВЛЕННЫЙ ОРГАНИЗАЦИЕЙ СЧЕТ НЕ СООТВЕТСТВУЕТ РАССЧИТАННОМУ ПРОГРАММОЙ ЗНАЧЕНИЮ! ОБРАТИТЕСЬ В АБОНОТДЕЛ!
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        return printBilling
    }
}

//----------------------НАЧАЛО----------------------

//Задаем дату для платежек
Bill.date = new Date()
Bill.date = Bill.date.copyWith(
        year: 2015,
        month: Calendar.OCTOBER,
        date: 15 )

//Создаем квартиру
def flat = new Flat() as AddressT
flat.with {
    //Адрес
    index = '18030'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Рябоконя'
    numHouse = 31
    numFlat = 41
    //Остальные значения
    allArea = 45.1
    heatArea = 44.1
    numRegistration = 2
    numAnimal = 1
    electroConsumerType = ElectroConsumerType.TYPE_1
    gasConsumerType = GasConsumerType.TYPE_1
    hotWaterConsumerType = HotWaterConsumerType.TYPE_2
    gasNormType = GasNormType.NORM_1
    hotWaterNormType = HotWaterNormType.NORM_2
    coldWaterNormType = ColdWaterNormType.NORM_1
    isGasCounter = false
    isHotWaterCounter = false
    isColdWaterCounter = false
    isCity = true
}

//Создаем плательщика
def payer = new Payer() as AddressT
payer.with {
    //Адрес
    index = '18008'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Ярославская'
    numHouse = '8/1'
    numFlat = '218'
    //Остальные значения
    firstName = 'Шашлюк'
    lastName = 'Алексей'
    patronymic = 'Валериевич'
    personalAccount = '41298042'
}

//Создаем ЧеркассыГазСбыт
def gasSbut = new Organisation().withTraits AddressT, BankT
gasSbut.with {
    //Адрес
    index = '18000'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Громова'
    numHouse = '142'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'Черкасское областное управление АО "Сбербанк"'
    checkingAccount = '26030301127727'
    MFO = '354507'
    //Наименование
    nameOrganisation = 'ООО "Черкассыгаз Сбыт"'
    ZKPO = '39672471'
}

//Создаем Облэнерго
def oblEnergo = new Organisation().withTraits AddressT, BankT
oblEnergo.with {
    //Адрес
    index = '18002'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Гоголя'
    numHouse = '285'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'Черкасское областное управление АО "Сбербанк"'
    checkingAccount = '26037300182'
    MFO = '354507'
    //Наименование
    nameOrganisation = 'ЗАО "Черкассыоблэнерго" Черкасский городской РЭС'
    ZKPO = '25204608'
}

//Создаем ТЭЦ
def chTEC = new Organisation().withTraits AddressT, BankT
chTEC.with {
    //Адрес
    index = '18000'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'проезд Химиков'
    numHouse = '76'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'Черкасское областное управление АО "Сбербанк"'
    checkingAccount = '26039341100255'
    MFO = '354507'
    //Наименование
    nameOrganisation = 'Черкасская ТЭЦ'
    ZKPO = '00204033'
}

//Создаем Службу Чистоты
def cleenService = new Organisation().withTraits AddressT, BankT
cleenService.with {
    //Адрес
    index = '18003'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Чайковского'
    numHouse = '117'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'ЧГРУ ЗАО КБ "Приватбанк"'
    checkingAccount = '26004060191291'
    MFO = '354347'
    //Наименование
    nameOrganisation = 'КП "Черкасская служба чистоты"'
    ZKPO = '03328652'
}

//Создаем Приднепровский СУБ
def dneprSUB = new Organisation().withTraits AddressT, BankT
dneprSUB.with {
    //Адрес
    index = '18005'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Ильина'
    numHouse = '330/5'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'ЧОУ АБ "Укргазбанк"'
    checkingAccount = '2600546919'
    MFO = '320478'
    //Наименование
    nameOrganisation = 'КП "Приднепровская ССД"'
    ZKPO = '36701792'
}

//Создаем Черкассыводоканал для централизованного водоснабжения
def waterChannel = new Organisation().withTraits AddressT, BankT
waterChannel.with {
    //Адрес
    index = '18036'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Ватутина'
    numHouse = '12'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'ЧГРУ ЗАО КБ "Приватбанк"'
    checkingAccount = '26003060347905'
    MFO = '354347'
    //Наименование
    nameOrganisation = 'КП "Черкассыводоканал"'
    ZKPO = '03357168'
}

//Создаем Черкассыводоканал для централизованного водоотведения
def waterChannelOut = new Organisation().withTraits AddressT, BankT
waterChannelOut.with {
    //Адрес
    index = '18036'
    country = 'Украина'
    region = 'Черкасская'
    city = 'Черкассы'
    street = 'Ватутина'
    numHouse = '12'
    numFlat = ''
    //Банковские реквизиты
    nameBank = 'АО "УкрСиббанк"'
    checkingAccount = '26003314673900'
    MFO = '354347'
    //Наименование
    nameOrganisation = 'КП "Черкассыводоканал"'
    ZKPO = '03357168'
}

//Считаем газ по норме
def gasNorm = new GasNorm()
gasNorm.with {
    numRegistration = flat.numRegistration
    numAnimal = flat.numAnimal
    heatArea = flat.heatArea
    gasNormType = flat.gasNormType
}

//Считаем горячую воду по норме
def hotWaterNorm = new HotWaterNorm()
hotWaterNorm.with {
    numRegistration = flat.numRegistration
    hotWaterNormType = flat.hotWaterNormType
}

//Считаем холодную воду по норме
def coldWaterNorm = new ColdWaterNorm()
coldWaterNorm.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = false
}

//Считаем водоотведение по норме
def coldWaterNormOut = new ColdWaterNorm()
coldWaterNormOut.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = true
}

//Считаем отопление по норме
def heatNorm = new HeatNorm()
heatNorm.with {
    heatArea = flat.heatArea
}

//Считаем квартплату СУБ по норме
def subNorm = new SubNorm()
subNorm.with {
    allArea = flat.allArea
}

//Считаем вывоз мусора по норме
def trashNorm = new TrashNorm()
trashNorm.with {
    numRegistration = flat.numRegistration
}

//Считаем отопление по счетчику
def heatCounter = new HeatCounter()
heatCounter.with {
    type = CounterType.HEAT
    model = 'GROSS ETR-UA'
    number = 1006
    ind_before = 24.08
    ind_now = 24.56
}

//Считаем горячую воду по счетчику
def hotWaterCounter = new HotWaterCounter()
hotWaterCounter.with {
    type = CounterType.HOT_WATER_1_TARIFF
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

//Считаем холодную воду по счетчику
def coldWaterCounter = new ColdWaterCounter()
coldWaterCounter.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = false
}

//Считаем водоотведение по счетчику
def coldWaterCounterOut = new ColdWaterCounter()
coldWaterCounterOut.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = true
}

//Считаем газ по счетчику
def gasCounter = new GasCounter()
gasCounter.with {
    type = CounterType.GAS
    model = 'ВИЗАР G4'
    number = 13400
    ind_before = 16826
    ind_now = 17403
    gasConsumerType = flat.gasConsumerType
}

//Считаем электричество по 1-тарифному счетчику
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

//Считаем электричество по 2-тарифному счетчику
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

//Считаем электричество по 3-тарифному счетчику
def elCounter_3 = new ThreeZoneElCounter().withTraits ThreeElectroZoneT
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
}

//Считаем электричество по 1-тарифному счетчику
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

//Платежка за газ
def gasBill = new Bill()
gasBill.with {
    client = payer
    organisation = gasSbut
    flatToPay = flat

    nameBill = 'ГАЗ'
    debt = 4.31
    recalculation = 0.00
    payed = 0.00
    subsidy = 10.04
    mustBePayed = 43.15
    isCounter = flat.isGasCounter

    counterOrNorm = isCounter ? gasCounter : gasNorm
}

//Платежка за электричество
def electroBill = new Bill()
electroBill.with {
    client = payer
    organisation = oblEnergo
    flatToPay = flat

    nameBill = 'ЭЛЕКТРИЧЕСТВО'
    debt = 296.30
    recalculation = 0.00
    payed = 596.30
    subsidy = 21.37
    mustBePayed = -224.48
    isCounter = true

    counterOrNorm = isCounter ? elCounter : elNorm
}

//Платежка за холодную воду
def coldWaterBill = new Bill()
coldWaterBill.with {
    client = payer
    organisation = waterChannel
    flatToPay = flat

    nameBill = 'ВОДОСНАБЖЕНИЕ (ХВС)'
    debt = 38.49
    recalculation = 3.60
    payed = 38.49
    subsidy = 8.94
    mustBePayed = 41.78
    isCounter = flat.isColdWaterCounter

    counterOrNorm = isCounter ? coldWaterCounter : coldWaterNorm
}

//Платежка за водоотведение
def coldWaterOutBill = new Bill()
coldWaterOutBill.with {
    client = payer
    organisation = waterChannelOut
    flatToPay = flat

    nameBill = 'ВОДООТВЕДЕНИЕ'
    debt = 59.92
    recalculation = -2.30
    payed = 59.92
    subsidy = 14.23
    mustBePayed = 57.13
    isCounter = flat.isColdWaterCounter

    counterOrNorm = isCounter ? coldWaterCounterOut : coldWaterNormOut
}

//Платежка за горячую воду
def hotWaterBill = new Bill()
hotWaterBill.with {
    client = payer
    organisation = chTEC
    flatToPay = flat

    nameBill = 'ВОДОСНАБЖЕНИЕ (ГВС)'
    debt = 204.33
    recalculation = -49.11
    payed = 204.33
    subsidy = 60.96
    mustBePayed = 137.94
    isCounter = flat.isHotWaterCounter

    counterOrNorm = isCounter ? hotWaterCounter : hotWaterNorm
}

//Платежка за отопление
def heatBill = new Bill()
heatBill.with {
    client = payer
    organisation = chTEC
    flatToPay = flat

    nameBill = 'ОТОПЛЕНИЕ'
    debt = -85.59
    recalculation = 0.00
    payed = 0.00
    subsidy = 0.00
    mustBePayed = -85.59
    isCounter = flat.isHeatCounter

    counterOrNorm = isCounter ? heatCounter : heatNorm
}

//Платежка СУБ
def subBill = new Bill()
subBill.with {
    client = payer
    organisation = dneprSUB
    flatToPay = flat

    nameBill = 'СУБ'
    debt = 64.35
    recalculation = 0.00
    payed = 64.35
    subsidy = 48.19
    mustBePayed = 62.71
    isCounter = false

    counterOrNorm = subNorm
}

//Платежка за вывоз мусора
def trashBill = new Bill()
trashBill.with {
    client = payer
    organisation = cleenService
    flatToPay = flat

    nameBill = 'ВЫВОЗ МУСОРА'
    debt = 9.21
    recalculation = 0.00
    payed = 9.21
    subsidy = 6.71
    mustBePayed = 9.01
    isCounter = false

    counterOrNorm = trashNorm
}


//--------------------ВЫВОД РЕЗУЛЬТАТОВ-------------------
println '-----------------------------------------СЕКЦИЯ 1-----------------------------------------------'
println '---------------------Простой вывод информации разнообразными способами--------------------------'
println '************************************************************************************************\n'

println "Адресс квартиры: $flat.fullAddress"
println "Описание квартиры через аннотацию: $flat.printName\n"

println "Адрес плательщика: $payer.fullAddress"
println "Полное имя плательщика: $payer.fullName"
println "Короткое имя плательщика: $payer.shortName"
println "Описание плательщика через аннотацию: $payer.printName\n"

//println gasSbut.fullName
//println gasSbut.fullAddress
//println gasSbut.fullRequisite
//println oblEnergo.fullName
//println oblEnergo.fullAddress
//println oblEnergo.fullRequisite
//println chTEC.fullName
//println chTEC.fullAddress
//println chTEC.fullRequisite
//println cleenService.fullName
//println cleenService.fullAddress
//println cleenService.fullRequisite
//println dneprSUB.fullName
//println dneprSUB.fullAddress
//println dneprSUB.fullRequisite
//println waterChannel.fullName
//println waterChannel.fullAddress
//println waterChannel.fullRequisite
//println waterChannelOut.fullName
//println waterChannelOut.fullAddress
//println waterChannelOut.fullRequisite

println "Газ по норме (без округления BigDecimal): $gasNorm.amount"
println "Горячая вода по норме (без округления BigDecimal): $hotWaterNorm.amount"
println "Водоснабжение по норме (без округления BigDecimal): $coldWaterNorm.amount"
println "Водоотведение по норме (без округления BigDecimal): $coldWaterNormOut.amount"
println "Отопление по норме (без округления BigDecimal): $heatNorm.amount"
println "СУБ (без округления BigDecimal): $subNorm.amount"
println "Вывоз мусора (без округления BigDecimal): $trashNorm.amount"

println "\nСчетчик тепла (через аннотацию): $heatCounter"
println "\nОтопление по счетчику - $heatCounter.amount"
println "\nСчетчик горячей воды (через аннотацию): $hotWaterCounter"
println "\nГорячая вода по счетчику - $hotWaterCounter.amount"
println "\nСчетчик холодной воды (потребление): $coldWaterCounter"
println "\nВодоснабжение по счетчику - $coldWaterCounter.amount"
println "\nСчетчик холодной воды (водоотведение, через аннотацию): $coldWaterCounterOut"
println "\nВодотведение по счетчику - $coldWaterCounterOut.amount"
println "\nСчетчик газа (через аннотацию): $gasCounter"
println "\nГаз по счетчику $gasCounter.amount"
println "\nСчетчик электроэнергии (1 тариф, через аннотацию): $elCounter.printName"
println "\n1-зонный электросчетчик с трейтом подсчета стоимости - $elCounter.amount"
println "\nСчетчик электроэнергии (2 тарифа, через аннотацию): $elCounter_2.printName"
println "\n2-зонный электросчетчик с трейтом подсчета стоимости - $elCounter_2.amount"
println "\nСчетчик электроэнергии (3 тарифа, через аннотацию): $elCounter_3.printName"
println "\n3-зонный электросчетчик с трейтом подсчета стоимости - $elCounter_3.amount"
println "\nСчетчик электроэнергии (1 тариф, по умолчанию, через аннотацию): $elCounter_4"
println "\n1-зонный дефолтный электросчетчик - $elCounter_4.amount"

println '\n-----------------------------------------СЕКЦИЯ 2-----------------------------------------------'
println '-------------------------------------Вывод платежек---------------------------------------------'
println '************************************************************************************************\n'

println "$gasBill.billing\n"
println "$electroBill.billing\n"
println "$coldWaterBill.billing\n"
println "$coldWaterOutBill.billing\n"
println "$hotWaterBill.billing\n"
println "$heatBill.billing\n"
println "$subBill.billing\n"
println "$trashBill.billing\n"