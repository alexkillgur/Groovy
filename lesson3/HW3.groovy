/**
 * Created by Killgur on 31.10.2015
 */
import groovy.transform.ToString

//���� ���������
enum CounterType {
    GAS, ELETRO_1_ZONE, ELETRO_2_ZONE, ELETRO_3_ZONE, COLD_WATER, HOT_WATER_1_TARIFF, HOT_WATER_4_TARIFF, HEAT
}

//���� ������������ �������������� �� ���������
enum ElectroConsumerType {
    TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, TYPE_7
}

//���� ������������ ���� �� ���������
enum GasConsumerType {
    TYPE_1, TYPE_2
}

//���� ������������ ���� �� �����
enum GasNormType {
    NORM_1, NORM_2, NORM_3, NORM_4, NORM_5
}

//���� ������������ ������� ���� �� ���������
enum HotWaterConsumerType {
    TYPE_1, TYPE_2
}

//���� ������������ ������� ���� �� �����
enum HotWaterNormType {
    NORM_1, NORM_2
}

//���� ������������ �������� ���� �� �����
enum ColdWaterNormType {
    NORM_1, NORM_2
}

//�����, ���������� ����� �����������/��������/�����������
trait AddressT {
    String index
    String country
    String region
    String city
    String street
    String numHouse
    String numFlat

    String getFullAddress() {
        def strAddr = "������: $index, ������: $country, �������: $region, �. $city, ��. $street, �. $numHouse"
        if ( numFlat )
            strAddr += ", ��. $numFlat"
        return strAddr
    }

    String getShortAddress() {
        def strAddr = "$index, �. $city, ��. $street, �. $numHouse"
        if ( numFlat )
            strAddr += ", ��. $numFlat"
        return strAddr
    }
}

//�����, ���������� ���������� ���������
trait BankT {
    String nameBank
    String checkingAccount
    String MFO
    String ZKPO

    String getFullRequisite() {
        "������������: $nameBank, �/�: $checkingAccount, ���: $MFO, ������ (����): $ZKPO"
    }
}

//��������� ��� ������ � ��������� � ����� �������
trait PrintClassT {
    def getPrintName() {
        this
    }
}

//����� ��������� ��������
interface CounterIF {
    //��������� �������� ����� � ������������ �������, ���� ����
    def setTariff()
    //��������� ��� �������� ��������� �����. �� �������� �������� �� ������������� ��������� ���������� �� ������, �.�. �� ����������� :)
    def getAmount()
}

//����� ��� ������������� ���������������
trait OneElectroZoneT {
    //������� �������� ��������� ������
    def getAmount() {
        setTariff()

        used_1 = []
        usedElectro_1 = ind_1_now - ind_1_before
        fillUsed( usedElectro_1, used_1 )

        return cycleOnUsed( used_1, zoneElFactor[0] )
    }

}

//����� ��� ������������� ���������������
trait TwoElectroZoneT implements OneElectroZoneT {
    //������� �������� ��������� ������
    def getAmount() {
        def sum = super.getAmount()

        used_2 = []
        usedElectro_2 = ind_2_now - ind_2_before
        fillUsed( usedElectro_2, used_2 )

        return sum + cycleOnUsed( used_2, zoneElFactor[1] )
    }
}

//����� ��� ������������� ���������������
trait ThreeElectroZoneT implements TwoElectroZoneT {
    //������� �������� ��������� ������
    def getAmount() {
        def sum = super.getAmount()

        used_3 = []
        usedElectro_3 = ind_3_now - ind_3_before
        fillUsed( usedElectro_3, used_3 )

        return sum + cycleOnUsed( used_3, zoneElFactor[2] )
    }
}

//����� ��� ������ �������� ��������
@ToString ( includeNames = true, includeFields = true )
abstract class Counter {
    CounterType type //��� ��������
    String model //������ ��������
    int number //����� ��������
    def tariff = [] //����� �������
    def ind_before //���������� ��������� ��������
    def ind_now //������� ��������� ��������
}

//������������� �������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
abstract class ElectroCounter extends Counter implements CounterIF, PrintClassT {
    def limitation = [] //����� �������

    ElectroConsumerType electroConsumerType //��� ������������������
    boolean isCity //�������� ��������� � ������ ��� ����

    //������� �������� ����� � ������� � ����������� �� ��������� ������������ ��������������
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
                // �� 09.2015 ������ �����
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
                // �� 09.2015 ������ �����
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

    //���������� ������� �������������� ��� �������� �������� ����� � ������� (������� ���� ������������ � ������ ������� �������)
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

    //����� �� ����� �������������� ��� � ����������� ��������� � ����������� �� ������� � ������������
    def cycleOnUsed( used, factor ) {
        def sum = 0
        used.eachWithIndex { it, i ->
            sum += factor*it*tariff[i]
        }
        return sum
    }

    //������� �������� ��������� ������ �� ���������
    def getAmount() {
        println '��������� �������������� �������� �� �������������'
        return 0
    }
}

//������������ ������������� �������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class OneZoneElCounter extends ElectroCounter {
    def used_1 = [] //������ �������������� ��� �������� ������� ��� �������� ��������� �������� �������
    def ind_1_before //���������� ��������� �������� �� 1-� ����
    def ind_1_now //������� ��������� �������� �� 1-� ����
    def usedElectro_1 //����� ��� ������������
    def zoneElFactor = [1] //������������ ��� ������������� ��������
}

//������������ ������������� �������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class TwoZoneElCounter extends OneZoneElCounter {
    def used_2 = [] //������������ ����� �������� ����� �������� � �����������. ���� �������
    def ind_2_before //���������� ��������� �������� �� 2-� ����
    def ind_2_now //������� ��������� �������� �� 2-� ����
    def usedElectro_2 //������������ ����� �������� ����� ��������� � �����������. ���� �������
    def zoneElFactor = [1, 0.7] //������������ ��� ������������� ��������
}

//������������ ������������� �������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ThreeZoneElCounter extends TwoZoneElCounter {
    def used_3 = [] //������������ ����� �������� ����� �������� � �����������. ���� �������
    def ind_3_before //���������� ��������� �������� �� 3-� ����
    def ind_3_now //������� ��������� �������� �� 3-� ����
    def usedElectro_3 //������������ ����� �������� ����� ��������� � �����������. ���� �������
    def zoneElFactor = [1.5, 1, 0.7] //������������ ��� ������������� ��������
}

//������� �������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class GasCounter extends Counter implements CounterIF {
    def limitation = [] //����� �������
    def usedGas //����� �3 ������������
    def used = [] //������ �������������� �3 �������� ������� ��� �������� ��������� �������� �������

    GasConsumerType gasConsumerType //��� ��������� ����

    //������� �������� ����� � ������� � ����������� �� ��������� ������������ ����
    def setTariff() {
        switch ( gasConsumerType ) {
            case GasConsumerType.TYPE_1:
                limitation = [ 0 ]
                tariff = [ 7.188 ]
                used << usedGas
                break
            case GasConsumerType.TYPE_2:
                // ����� � ��� �� ��������
                if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
                    limitation = [ 0 ]
                    tariff = [ 7.188 ]
                    used << usedGas
                }
                // ����� � ������� �� ������
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

    //������� �������� ��������� ������
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

//������� �������� ����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ColdWaterCounter extends Counter implements CounterIF {
    def usedColdWater //����� �3 ������������
    boolean isWaterOut //�������������

    //������� �������� �����
    def setTariff() {
        tariff = isWaterOut ? [ 4.092 ] : [ 4.284 ]
    }

    //������� �������� ��������� ������
    def getAmount() {
        usedColdWater = ind_now - ind_before
        setTariff()

        return usedColdWater*tariff[0]
    }
}

//������� ������� ����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HotWaterCounter extends Counter implements CounterIF {
    def usedHotWater //����� �3 ������������
    def used = [] //������ �������������� �3 �������� ���������� �������������� ��������
    def zoneHotWaterFactor = [1, 0.9, 0.7, 0 ] //������������ ��� ���������������� ��������

    HotWaterConsumerType hotWaterConsumerType //��� ��������� ������� ����

    //������� �������� �����
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

    //������� �������� ��������� ������
    def getAmount() {
        setTariff()
        switch ( type ) {
        //������� �������
            case CounterType.HOT_WATER_1_TARIFF:
                usedHotWater = ind_now - ind_before
                return usedHotWater*tariff[0]
        //������������� �������
            case CounterType.HOT_WATER_4_TARIFF:
                def sum = 0
                used.eachWithIndex { it, i ->
                    sum += it*tariff[0]*zoneHotWaterFactor[i]
                }
                return sum += used[3]*tariff[1]
        }

    }
}

//������� �����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HeatCounter extends Counter implements CounterIF {
    def usedHeat //����� ���� ������������

    //������� �������� �����
    def setTariff() {
        tariff = [ 642.09 ]
    }

    //������� �������� ��������� ������
    def getAmount() {
        usedHeat = ind_now - ind_before
        setTariff()

        return usedHeat*tariff[0]
    }
}

//���������� ����� ���� �����������
@ToString ( includeNames = true, includeFields = true )
abstract class Norm {
    def norm //�����
    def tariff //�����

    //������� �������� ��������� ������
    def getAmount() {
        setTariff()
        return norm*tariff
    }
}

//����� ����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class GasNorm extends Norm implements CounterIF {
    int numRegistration //���������� ����������� �������
    int numAnimal //���������� ��������
    def heatArea //������������ �������

    GasNormType gasNormType //��� �����

    //������� �������� ����� (�����)
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
                // ����� � ��� �� ��������
                if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
                    norm = ( numRegistration + numAnimal <= 3 ) ? 70 : ( 70 + 11*( numRegistration + numAnimal - 3 ) )
                }
                // ����� � ������� �� ������
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

//����� ������� ����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HotWaterNorm extends Norm implements CounterIF {
    int numRegistration //���������� ����������� �������

    HotWaterNormType hotWaterNormType //��� �����

    //������� �������� ����� (�����)
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

//����� �������� ����
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class ColdWaterNorm extends Norm implements CounterIF {
    int numRegistration //���������� ����������� �������
    boolean isWaterOut //�������������

    ColdWaterNormType coldWaterNormType //��� �����

    //������� �������� ����� (�����)
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

//����� ���������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class HeatNorm extends Norm implements CounterIF {
    def heatArea //������������ �������

    //������� �������� ����� (�����)
    def setTariff() {
        norm = heatArea
        // ����� � ��� �� ��������
        if ( Bill.date[Calendar.MONTH] >= 4 && Bill.date[Calendar.MONTH] <= 8) {
            tariff = 0
        }
        // ����� � ������� �� ������
        else {
            tariff = 16.42
        }
    }
}

//���
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class SubNorm extends Norm implements CounterIF {
    def allArea //����� �������

    //������� �������� ����� (�����)
    def setTariff() {
        norm = allArea
        tariff = 2.459
    }
}

//����� ������
@ToString ( includeNames = true, includeFields = true, includeSuper = true )
class TrashNorm extends Norm implements CounterIF {
    int numRegistration //���������� ����������� �������

    //������� �������� ����� (�����)
    def setTariff() {
        norm = numRegistration
        tariff = 7.86
    }
}

//����� ��� �������� ��������, �� ������� ������ ����������
@ToString ( includeNames = true, includeFields = true )
class Flat implements PrintClassT {
    def allArea //����� �������
    def heatArea //������������ �������
    int numRegistration //���������� �����������
    int numAnimal //���������� ��������
    boolean isCity //�������� ��������� � ������ ��� ����
    ElectroConsumerType electroConsumerType //��� ������������������ �� ���������
    GasConsumerType gasConsumerType //��� ����������� ���� �� ���������
    GasNormType gasNormType //��� ����������� ���� �� �����
    HotWaterConsumerType hotWaterConsumerType //��� ����������� ������� ���� �� ���������
    HotWaterNormType hotWaterNormType //��� ����������� ������� ���� �� �����
    ColdWaterNormType coldWaterNormType //��� ����������� �������� ���� �� �����
    boolean isGasCounter //���� �� ������� �������
    boolean isHotWaterCounter //���� �� ������� ������� ����
    boolean isColdWaterCounter //���� �� ������� �������� ����
    boolean isHeatCounter //���� �� ������� �����
}

//����� ��� �������� �����������, ������� ��������� ����
@ToString ( includeNames = true, includeFields = true )
class Organisation implements PrintClassT {
    String nameOrganisation

    String getFullName() {
        "������������: $nameOrganisation"
    }
}

//����������� ��������� ���������� � �������� �������. ������ ������ ����� ���� � mrhaki � ������� ������������ :)
interface TransformerIF {
    String transform(String[] str)
}

//���������� ������ �� ��������� ������� �����
trait DefaultTransformerT implements TransformerIF {
    String transform(String[] str) {
        def sb = new StringBuilder()
        str.each { it -> sb.append "$it " }
        return sb
    }
}

//��������� ��� �������� ������� ����� � ������� �������
trait UpperT implements TransformerIF {
    String transform(String[] str) {
        str.eachWithIndex { it, i -> str[i] = it.toUpperCase() }
        super.transform( str )
    }
}

//����� ��� ������ �������� �����������
@ToString ( includeNames = true, includeFields = true )
class Payer implements PrintClassT, DefaultTransformerT, UpperT {
    String firstName
    String lastName
    String patronymic
    String personalAccount

    String getFullName() {
        "���: $firstName, �������: $lastName, ��������: $patronymic, �/�: $personalAccount"
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

//����� ��� ������ �������� ��������
@ToString ( includeNames = true, includeFields = true )
class Bill implements BillingIF, PrintClassT {
    static date //�����, �� ������� ��������� ����

    String nameBill
    def debt //���� �� ������ ������
    def recalculation //����������
    def payed //�������� �� ������ ������
    def subsidy //��������� ��������
    def mustBePayed //������� � ������
    boolean isCounter //���� �� ��������

    def client //�����, ������� ������
    def organisation //�����������, ����������� ����
    def flatToPay //��������, �� ������� ������
    def counterOrNorm //���� ������� �� �����, ���� �� ��������

    //������ ����� ������
    def getMonth() {
        switch ( date[Calendar.MONTH] ) {
            case 0: return '������'
            case 1: return '�������'
            case 2: return '����'
            case 3: return '������'
            case 4: return '���'
            case 5: return '����'
            case 6: return '����'
            case 7: return '������'
            case 8: return '��������'
            case 9: return '�������'
            case 10: return '������'
            case 11: return '�������'
        }
    }

    //������������ ������ �� ����� ��� �����
    def getCounted() {
        ( counterOrNorm.amount ).setScale( 2, BigDecimal.ROUND_HALF_UP )
    }

    //����������� � ������
    def getToPay() {
        ( debt + counted + recalculation - payed - subsidy ).setScale( 2, BigDecimal.ROUND_HALF_UP )
    }

    //���������� ��������� ������������ � ������ � ��������� �������� (�����)
    boolean isValidate() {
        mustBePayed == toPay
    }

    //�������� ��������
    def getBilling() {
        def printBilling =
                """
        -----------------------------------------------------${nameBill}---------------------------------------------------------
        ����������: $client.shortName
        ����� �����������: $client.shortAddress
        ����� ��������: $flatToPay.shortAddress
        �����������: $organisation.fullName
        ����� �����������: $organisation.shortAddress
        ���������: $organisation.fullRequisite
        ���� ��: $month
        -------------------------------------------------------------------------------------------------------------------------------------
        ����: $debt | ����������: $recalculation | ������: $payed | ��������: $subsidy | ���������� ������������ � ������: $mustBePayed |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        if ( isCounter ) {
            printBilling +=
                    """                                                                  ��������� � ������ �� ��������: $counted |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        else {
            printBilling +=
                    """                                                                     ��������� � ������ �� �����: $counted |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        printBilling +=
                """                                 ��������� ���������� � ������ � ������ ��������� �������� (�����): $toPay |
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        if ( !validate ) {
            printBilling +=
                    """          ��������! ������������ ������������ ���� �� ������������� ������������� ���������� ��������! ���������� � ���������!
        -------------------------------------------------------------------------------------------------------------------------------------
        """
        }
        return printBilling
    }
}

//----------------------������----------------------

//������ ���� ��� ��������
Bill.date = new Date()
Bill.date = Bill.date.copyWith(
        year: 2015,
        month: Calendar.OCTOBER,
        date: 15 )

//������� ��������
def flat = new Flat() as AddressT
flat.with {
    //�����
    index = '18030'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '��������'
    numHouse = 31
    numFlat = 41
    //��������� ��������
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

//������� �����������
def payer = new Payer() as AddressT
payer.with {
    //�����
    index = '18008'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '�����������'
    numHouse = '8/1'
    numFlat = '218'
    //��������� ��������
    firstName = '������'
    lastName = '�������'
    patronymic = '����������'
    personalAccount = '41298042'
}

//������� ���������������
def gasSbut = new Organisation().withTraits AddressT, BankT
gasSbut.with {
    //�����
    index = '18000'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '�������'
    numHouse = '142'
    numFlat = ''
    //���������� ���������
    nameBank = '���������� ��������� ���������� �� "��������"'
    checkingAccount = '26030301127727'
    MFO = '354507'
    //������������
    nameOrganisation = '��� "����������� ����"'
    ZKPO = '39672471'
}

//������� ���������
def oblEnergo = new Organisation().withTraits AddressT, BankT
oblEnergo.with {
    //�����
    index = '18002'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '������'
    numHouse = '285'
    numFlat = ''
    //���������� ���������
    nameBank = '���������� ��������� ���������� �� "��������"'
    checkingAccount = '26037300182'
    MFO = '354507'
    //������������
    nameOrganisation = '��� "�����������������" ���������� ��������� ���'
    ZKPO = '25204608'
}

//������� ���
def chTEC = new Organisation().withTraits AddressT, BankT
chTEC.with {
    //�����
    index = '18000'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '������ �������'
    numHouse = '76'
    numFlat = ''
    //���������� ���������
    nameBank = '���������� ��������� ���������� �� "��������"'
    checkingAccount = '26039341100255'
    MFO = '354507'
    //������������
    nameOrganisation = '���������� ���'
    ZKPO = '00204033'
}

//������� ������ �������
def cleenService = new Organisation().withTraits AddressT, BankT
cleenService.with {
    //�����
    index = '18003'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '�����������'
    numHouse = '117'
    numFlat = ''
    //���������� ���������
    nameBank = '���� ��� �� "����������"'
    checkingAccount = '26004060191291'
    MFO = '354347'
    //������������
    nameOrganisation = '�� "���������� ������ �������"'
    ZKPO = '03328652'
}

//������� �������������� ���
def dneprSUB = new Organisation().withTraits AddressT, BankT
dneprSUB.with {
    //�����
    index = '18005'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '������'
    numHouse = '330/5'
    numFlat = ''
    //���������� ���������
    nameBank = '��� �� "����������"'
    checkingAccount = '2600546919'
    MFO = '320478'
    //������������
    nameOrganisation = '�� "�������������� ���"'
    ZKPO = '36701792'
}

//������� ����������������� ��� ����������������� �������������
def waterChannel = new Organisation().withTraits AddressT, BankT
waterChannel.with {
    //�����
    index = '18036'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '��������'
    numHouse = '12'
    numFlat = ''
    //���������� ���������
    nameBank = '���� ��� �� "����������"'
    checkingAccount = '26003060347905'
    MFO = '354347'
    //������������
    nameOrganisation = '�� "�����������������"'
    ZKPO = '03357168'
}

//������� ����������������� ��� ����������������� �������������
def waterChannelOut = new Organisation().withTraits AddressT, BankT
waterChannelOut.with {
    //�����
    index = '18036'
    country = '�������'
    region = '����������'
    city = '��������'
    street = '��������'
    numHouse = '12'
    numFlat = ''
    //���������� ���������
    nameBank = '�� "����������"'
    checkingAccount = '26003314673900'
    MFO = '354347'
    //������������
    nameOrganisation = '�� "�����������������"'
    ZKPO = '03357168'
}

//������� ��� �� �����
def gasNorm = new GasNorm()
gasNorm.with {
    numRegistration = flat.numRegistration
    numAnimal = flat.numAnimal
    heatArea = flat.heatArea
    gasNormType = flat.gasNormType
}

//������� ������� ���� �� �����
def hotWaterNorm = new HotWaterNorm()
hotWaterNorm.with {
    numRegistration = flat.numRegistration
    hotWaterNormType = flat.hotWaterNormType
}

//������� �������� ���� �� �����
def coldWaterNorm = new ColdWaterNorm()
coldWaterNorm.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = false
}

//������� ������������� �� �����
def coldWaterNormOut = new ColdWaterNorm()
coldWaterNormOut.with {
    numRegistration = flat.numRegistration
    coldWaterNormType = flat.coldWaterNormType
    isWaterOut = true
}

//������� ��������� �� �����
def heatNorm = new HeatNorm()
heatNorm.with {
    heatArea = flat.heatArea
}

//������� ���������� ��� �� �����
def subNorm = new SubNorm()
subNorm.with {
    allArea = flat.allArea
}

//������� ����� ������ �� �����
def trashNorm = new TrashNorm()
trashNorm.with {
    numRegistration = flat.numRegistration
}

//������� ��������� �� ��������
def heatCounter = new HeatCounter()
heatCounter.with {
    type = CounterType.HEAT
    model = 'GROSS ETR-UA'
    number = 1006
    ind_before = 24.08
    ind_now = 24.56
}

//������� ������� ���� �� ��������
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

//������� �������� ���� �� ��������
def coldWaterCounter = new ColdWaterCounter()
coldWaterCounter.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = false
}

//������� ������������� �� ��������
def coldWaterCounterOut = new ColdWaterCounter()
coldWaterCounterOut.with {
    type = CounterType.COLD_WATER
    model = 'GROSS ETR-UA'
    number = 19006
    ind_before = 21.6
    ind_now = 45.4
    isWaterOut = true
}

//������� ��� �� ��������
def gasCounter = new GasCounter()
gasCounter.with {
    type = CounterType.GAS
    model = '����� G4'
    number = 13400
    ind_before = 16826
    ind_now = 17403
    gasConsumerType = flat.gasConsumerType
}

//������� ������������� �� 1-��������� ��������
def elCounter = new OneZoneElCounter() as OneElectroZoneT
elCounter.with {
    type = CounterType.ELETRO_1_ZONE
    model = '���1-1-1-�-�2-�'
    number = 18100
    ind_1_before = 18660
    ind_1_now = 18825
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

//������� ������������� �� 2-��������� ��������
def elCounter_2 = new TwoZoneElCounter() as TwoElectroZoneT
elCounter_2.with {
    type = CounterType.ELETRO_2_ZONE
    model = '��� 1-4�'
    number = 19100
    ind_1_before = 18660
    ind_1_now = 18770
    ind_2_before = 18160
    ind_2_now = 18215
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

//������� ������������� �� 3-��������� ��������
def elCounter_3 = new ThreeZoneElCounter().withTraits ThreeElectroZoneT
elCounter_3.with {
    type = CounterType.ELETRO_3_ZONE
    model = '��� 1-4�'
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

//������� ������������� �� 1-��������� ��������
def elCounter_4 = new OneZoneElCounter()
elCounter.with {
    type = CounterType.ELETRO_1_ZONE
    model = '���1-1-1-�-�2-�'
    number = 18100
    ind_1_before = 18660
    ind_1_now = 18825
    electroConsumerType = flat.electroConsumerType
    isCity = flat.isCity
}

//�������� �� ���
def gasBill = new Bill()
gasBill.with {
    client = payer
    organisation = gasSbut
    flatToPay = flat

    nameBill = '���'
    debt = 4.31
    recalculation = 0.00
    payed = 0.00
    subsidy = 10.04
    mustBePayed = 43.15
    isCounter = flat.isGasCounter

    counterOrNorm = isCounter ? gasCounter : gasNorm
}

//�������� �� �������������
def electroBill = new Bill()
electroBill.with {
    client = payer
    organisation = oblEnergo
    flatToPay = flat

    nameBill = '�������������'
    debt = 296.30
    recalculation = 0.00
    payed = 596.30
    subsidy = 21.37
    mustBePayed = -224.48
    isCounter = true

    counterOrNorm = isCounter ? elCounter : elNorm
}

//�������� �� �������� ����
def coldWaterBill = new Bill()
coldWaterBill.with {
    client = payer
    organisation = waterChannel
    flatToPay = flat

    nameBill = '������������� (���)'
    debt = 38.49
    recalculation = 3.60
    payed = 38.49
    subsidy = 8.94
    mustBePayed = 41.78
    isCounter = flat.isColdWaterCounter

    counterOrNorm = isCounter ? coldWaterCounter : coldWaterNorm
}

//�������� �� �������������
def coldWaterOutBill = new Bill()
coldWaterOutBill.with {
    client = payer
    organisation = waterChannelOut
    flatToPay = flat

    nameBill = '�������������'
    debt = 59.92
    recalculation = -2.30
    payed = 59.92
    subsidy = 14.23
    mustBePayed = 57.13
    isCounter = flat.isColdWaterCounter

    counterOrNorm = isCounter ? coldWaterCounterOut : coldWaterNormOut
}

//�������� �� ������� ����
def hotWaterBill = new Bill()
hotWaterBill.with {
    client = payer
    organisation = chTEC
    flatToPay = flat

    nameBill = '������������� (���)'
    debt = 204.33
    recalculation = -49.11
    payed = 204.33
    subsidy = 60.96
    mustBePayed = 137.94
    isCounter = flat.isHotWaterCounter

    counterOrNorm = isCounter ? hotWaterCounter : hotWaterNorm
}

//�������� �� ���������
def heatBill = new Bill()
heatBill.with {
    client = payer
    organisation = chTEC
    flatToPay = flat

    nameBill = '���������'
    debt = -85.59
    recalculation = 0.00
    payed = 0.00
    subsidy = 0.00
    mustBePayed = -85.59
    isCounter = flat.isHeatCounter

    counterOrNorm = isCounter ? heatCounter : heatNorm
}

//�������� ���
def subBill = new Bill()
subBill.with {
    client = payer
    organisation = dneprSUB
    flatToPay = flat

    nameBill = '���'
    debt = 64.35
    recalculation = 0.00
    payed = 64.35
    subsidy = 48.19
    mustBePayed = 62.71
    isCounter = false

    counterOrNorm = subNorm
}

//�������� �� ����� ������
def trashBill = new Bill()
trashBill.with {
    client = payer
    organisation = cleenService
    flatToPay = flat

    nameBill = '����� ������'
    debt = 9.21
    recalculation = 0.00
    payed = 9.21
    subsidy = 6.71
    mustBePayed = 9.01
    isCounter = false

    counterOrNorm = trashNorm
}


//--------------------����� �����������-------------------
println '-----------------------------------------������ 1-----------------------------------------------'
println '---------------------������� ����� ���������� �������������� ���������--------------------------'
println '************************************************************************************************\n'

println "������ ��������: $flat.fullAddress"
println "�������� �������� ����� ���������: $flat.printName\n"

println "����� �����������: $payer.fullAddress"
println "������ ��� �����������: $payer.fullName"
println "�������� ��� �����������: $payer.shortName"
println "�������� ����������� ����� ���������: $payer.printName\n"

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

println "��� �� ����� (��� ���������� BigDecimal): $gasNorm.amount"
println "������� ���� �� ����� (��� ���������� BigDecimal): $hotWaterNorm.amount"
println "������������� �� ����� (��� ���������� BigDecimal): $coldWaterNorm.amount"
println "������������� �� ����� (��� ���������� BigDecimal): $coldWaterNormOut.amount"
println "��������� �� ����� (��� ���������� BigDecimal): $heatNorm.amount"
println "��� (��� ���������� BigDecimal): $subNorm.amount"
println "����� ������ (��� ���������� BigDecimal): $trashNorm.amount"

println "\n������� ����� (����� ���������): $heatCounter"
println "\n��������� �� �������� - $heatCounter.amount"
println "\n������� ������� ���� (����� ���������): $hotWaterCounter"
println "\n������� ���� �� �������� - $hotWaterCounter.amount"
println "\n������� �������� ���� (�����������): $coldWaterCounter"
println "\n������������� �� �������� - $coldWaterCounter.amount"
println "\n������� �������� ���� (�������������, ����� ���������): $coldWaterCounterOut"
println "\n������������ �� �������� - $coldWaterCounterOut.amount"
println "\n������� ���� (����� ���������): $gasCounter"
println "\n��� �� �������� $gasCounter.amount"
println "\n������� �������������� (1 �����, ����� ���������): $elCounter.printName"
println "\n1-������ �������������� � ������� �������� ��������� - $elCounter.amount"
println "\n������� �������������� (2 ������, ����� ���������): $elCounter_2.printName"
println "\n2-������ �������������� � ������� �������� ��������� - $elCounter_2.amount"
println "\n������� �������������� (3 ������, ����� ���������): $elCounter_3.printName"
println "\n3-������ �������������� � ������� �������� ��������� - $elCounter_3.amount"
println "\n������� �������������� (1 �����, �� ���������, ����� ���������): $elCounter_4"
println "\n1-������ ��������� �������������� - $elCounter_4.amount"

println '\n-----------------------------------------������ 2-----------------------------------------------'
println '-------------------------------------����� ��������---------------------------------------------'
println '************************************************************************************************\n'

println "$gasBill.billing\n"
println "$electroBill.billing\n"
println "$coldWaterBill.billing\n"
println "$coldWaterOutBill.billing\n"
println "$hotWaterBill.billing\n"
println "$heatBill.billing\n"
println "$subBill.billing\n"
println "$trashBill.billing\n"