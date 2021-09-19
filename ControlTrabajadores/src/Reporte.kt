import java.time.LocalDate

class Reporte(personal: Personal, horarios: List<Horario>, registros: List<Registro>) {

    public var retardos = 0
    public var faltas = 0
    public var permisos = 0
    private val persona = personal
    private val registros = registros
    private val horarios = horarios
    private var retardosQuincena = 0

    fun generarReporte(fechaInicial: String, fechaFinal: String) {
        var diaActual: LocalDate = LocalDate.parse(fechaInicial)
        var fin: LocalDate = LocalDate.parse(fechaFinal)

        println("Generando reporte de: " + persona.nombre)
        println("Desde " + diaActual + " a " + fin)
        println("********* Inicio del reporte *********")

        //loop que se ejecuta mientras el dia actual sea menor que la fecha final
        while (diaActual.isBefore(fin)) {
            // verificar que se resetee el valor de retardos por quincena para aplicar correctamente la sanción por 3 retardos
            if (retardosQuincena> 0 && ( (diaActual.dayOfMonth == 16)) || diaActual.minusDays(1).month.value < diaActual.month.value) {
                println("---- Fin de la quincena ----")
                retardosQuincena = 0
            }
            println("- Fecha: " + diaActual)
            var encontrado = false
            // recorrer los horarios y verificarlo si se encuentra uno aplicable al dia actual
            for (horario in horarios) {
                if (horario.dia == diaActual.dayOfWeek.value) {
                    encontrado = verificarHorario(horario, diaActual)
                }
            }

            if (!encontrado)
                println("No Aplica")

//            se aumenta el dia actual
            diaActual = diaActual.plusDays(1)
        }
        println()
        println("********* fin del reporte *********")
        println("retardos: " + retardos)
        println("faltas: " + faltas)
    }

    private fun verificarHorario(horario: Horario, diaActual: LocalDate): Boolean {
        // variable para verificar si se encontró un registro con el día buscado
        var encontrado: Boolean = false
        println("verificando Horario: " + diaActual)
//      se recorren los registros y se calculan las faltas y retardos si se encuentra el registro con la fecha actual
        for (registro in registros) {
            var fecha: LocalDate = LocalDate.parse(registro.fecha)
            if (diaActual == fecha) {
                calcularRetardo(registro.horaEntrada, horario.horaEntrada)
                calcularFalta(registro.horaSalida, horario.horaSalida)
                encontrado = true
            }
        }
        if (encontrado)
            return true


        println("Ausente")
        faltas++
        return false
    }

    fun calcularRetardo(horaE: String, horaIH: String): Int {
        val antiguedad = persona.antiguedad

        val diferenciaHorario = restarHora(horaE, horaIH)
        if (diferenciaHorario > 10 && diferenciaHorario < 20) {
            println("retardo")
            retardos++
            retardosQuincena++
            if ((antiguedad < 10) && (retardosQuincena % 3 == 0)) {
                println("falta")
                faltas++
            }
        }

        return 0
    }

    fun calcularFalta(horaF: String, horaFH: String): Int {

        val diferenciaHorario = restarHora(horaF, horaFH)
        if (diferenciaHorario < 0) {
            println("falta por salir antes")
            faltas++
        }
        return 0
    }


    private fun restarHora(horaEntrada: String, horaReal: String): Int {
//        "9:50" - "10:00"
        var hora1 = horaEntrada.split(":") // ["9","50"]
        var hora2 = horaReal.split(":")

        var horaSplit = hora1[0].toInt()//9
        var hora2Split = hora2[0].toInt()//10

        var minutoSplit = hora1[1].toInt()//50
        var minuto2Split = hora2[1].toInt()//00

        var horasAMinutos = horaSplit * 60 + minutoSplit //540
        var horasAMinutos2 = hora2Split * 60 + minuto2Split// 600

        var difHora = horasAMinutos - horasAMinutos2 // 9-10 = -1

        return difHora

    }

}






