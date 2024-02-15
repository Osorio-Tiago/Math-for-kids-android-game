package com.example.proyecto2movilessantiago

import android.annotation.SuppressLint
import android.content.ContentValues
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.proyecto2movilessantiago.databinding.FragmentLevelsBinding

class LevelsFragment : Fragment() {


    //Linkeo de todos los objetos en el layout
    private lateinit var binding: FragmentLevelsBinding


    //Media player
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerMario: MediaPlayer
    private lateinit var mediaPlayerLoose: MediaPlayer

    //Numeros
    private var numRand1 = -1
    private var numRand2 = -1

    private lateinit var nombreJugador: String
    private var resultado = 0
    private var score = 0
    private var vidas = 3
    private var nivel = 1
    private var scoreLimite = 9
    private var tituloNivel="Sumas"
    private var mensajeNivel = "Nivel $nivel - $tituloNivel"
    private lateinit var stringScoreAux: String
    private var toastMessage: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_levels, container, false)

        toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
        toastMessage?.show()

        //Media player
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mediaPlayer.start()
        mediaPlayer.isLooping = true

        mediaPlayerMario = MediaPlayer.create(requireContext(), R.raw.wonderful)
        mediaPlayerLoose = MediaPlayer.create(requireContext(), R.raw.bad)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLevelsBinding.bind(view)

        nombreJugador = arguments?.getString("player")!!
        val nicknameParam ="player: $nombreJugador"
        binding.tvNombre.text =  nicknameParam


        val stringScore = arguments?.getString("score")
        score = stringScore?.toInt() ?: 0
        stringScoreAux = "Score: $score"
        binding.tvScore.text = stringScore

        val stringVidas = arguments?.getString("vidas")
        vidas = stringVidas?.toInt() ?: 3
        updateVidasUI()

        numeroAleatorio()

        binding.comprobar.setOnClickListener { comparar() }
    }

    private fun numeroAleatorio() {
        if (score <= scoreLimite) {
            when(nivel){
                1-> nivel1()
                2-> nivel2()
                3-> nivel3()
                4-> nivel4()
                5-> nivel5()
                6-> nivel6()
                7-> nivel7()
                8,9-> nivel8()
            }
        } else {
            if(nivel<=8) {
                scoreLimite += 10
                nivel++
            }
            else {
                scoreLimite = Int.MAX_VALUE
            }
            actualizarMensajeMetodo()

            toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
            toastMessage?.show()
            numeroAleatorio()
        }
    }

    private fun actualizarMensajeMetodo() {
        when(nivel) {
            2 -> tituloNivel = "Sumas intermedias"
            3 -> tituloNivel = "Restas básicas"
            4 -> tituloNivel = "Sumas y restas"
            5 -> tituloNivel = "Multiplicación"
            6 -> tituloNivel = "División"
            7 -> tituloNivel = "Multiplicacion y division"
        }
        mensajeNivel = "Nivel $nivel - $tituloNivel"
    }

    private fun getDrawableId(numero: Int): Int {
        return when (numero) {
            0 -> R.drawable.cero
            1 -> R.drawable.uno
            2 -> R.drawable.dos
            3 -> R.drawable.tres
            4 -> R.drawable.cuatro
            5 -> R.drawable.cinco
            6 -> R.drawable.seis
            7 -> R.drawable.siete
            8 -> R.drawable.ocho
            9 -> R.drawable.nueve
            else -> throw IllegalArgumentException("Número inválido: $numero")
        }
    }

    @SuppressLint("Recycle")
    fun baseDeDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext(), "DB", null, 1)
        val db = admin.writableDatabase
        val consulta = db.rawQuery(
            "select * from puntaje where score = (select max(score) from puntaje)", null)
        if (consulta.moveToFirst()) {
            val temp_nombre = consulta.getString(0)
            val temp_score = consulta.getString(1)

            val bestScore = temp_score.toInt()

            if (score > bestScore) {
                val modificacion = ContentValues()
                modificacion.put("nombre", binding.tvNombre.text.toString())
                modificacion.put("score", score)
                db.update("puntaje", modificacion, "score=$bestScore", null)
            }
            } else {
                val insertar = ContentValues()
                insertar.put("nombre", nombreJugador)
                insertar.put("score", binding.tvScore.text.toString().toInt())
                db.insert("puntaje", null, insertar)
            }
        db.close()
    }

    private fun comparar() {
        var manzanas = "manzanas"
        var quedan = "quedan"
        val respuesta = binding.etRespuesta.text.toString()
        if (respuesta.isNotEmpty()) {
            val respuestaJugador = respuesta.toInt()

            if (respuestaJugador == resultado) {
                mediaPlayerMario.start()
                score ++
                stringScoreAux = "Score: $score"
                binding.tvScore.text = stringScoreAux

                toastMessage = Toast.makeText(requireContext(), "Excelente!", Toast.LENGTH_SHORT)
                toastMessage?.show()
            } else {
                mediaPlayerLoose.start()
                vidas--
                updateVidasUI()

                if (vidas == 0) {
                    toastMessage = Toast.makeText(requireContext(), "¡Has perdido!", Toast.LENGTH_SHORT)
                    toastMessage?.show()

                    val fragment = InitialFragment()
                    val bundle = Bundle()
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .commit()
                }
                else {
                    if(vidas == 1) {
                        manzanas = "manzana"
                        quedan = "queda"
                    }
                    toastMessage = Toast.makeText(requireContext(), "Te equivocaste! Te $quedan $vidas $manzanas!", Toast.LENGTH_SHORT)
                    toastMessage?.show()
                }
            }
            baseDeDatos()
            binding.etRespuesta.setText("")
            numeroAleatorio()
        } else {
            toastMessage = Toast.makeText(requireContext(), "Debes dar una respuesta", Toast.LENGTH_SHORT)
            toastMessage?.show()
        }
    }

    private fun updateVidasUI() {
        when (vidas) {
            3 -> {
                binding.ivManzanas.setImageResource(R.drawable.tresvidas)
            }
            2 -> {
                binding.ivManzanas.setImageResource(R.drawable.dosvidas)
            }
            1 -> {
                binding.ivManzanas.setImageResource(R.drawable.unavida)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayerMario.release()
        mediaPlayerLoose.release()
    }

    private fun nivel1(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()

        resultado = numRand1 + numRand2
        if (resultado > 10) {
            nivel1()
        }
        else {
            binding.ivNumUno.setImageResource(getDrawableId(numRand1))
            binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
        }
    }

    private fun nivel2(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        resultado = numRand1 + numRand2
        binding.ivNumUno.setImageResource(getDrawableId(numRand1))
        binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
        binding.imOperando.setImageResource(R.drawable.adicion)
    }

    private fun nivel3(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        resultado = numRand1 - numRand2
        if (resultado < 0) {
            nivel3()
        }
        else {
            binding.ivNumUno.setImageResource(getDrawableId(numRand1))
            binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
            binding.imOperando.setImageResource(R.drawable.resta)
        }
    }

    private fun nivel4(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        if (numRand1 > 0 && numRand2 <= 4) {
            resultado = numRand1 + numRand2
            binding.imOperando.setImageResource(R.drawable.adicion)
        } else {
            resultado = (numRand1 - numRand2)
            binding.imOperando.setImageResource(R.drawable.resta)
            if(resultado<0)
                nivel4()
        }
        binding.ivNumUno.setImageResource(getDrawableId(numRand1))
        binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
    }

    private fun nivel5(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        resultado = numRand1 * numRand2
        binding.ivNumUno.setImageResource(getDrawableId(numRand1))
        binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
        binding.imOperando.setImageResource(R.drawable.multiplicacion)
    }

    private fun nivel6(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        if(numRand2 == 0)
            nivel6()
        else if(numRand1 % numRand2 != 0) {
            nivel6()
        }
        else{
            resultado = numRand1 / numRand2
            binding.ivNumUno.setImageResource(getDrawableId(numRand1))
            binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
            binding.imOperando.setImageResource(R.drawable.division)
        }
    }

    private fun nivel7(){
        numRand1 = (Math.random() * 10).toInt()
        numRand2 = (Math.random() * 10).toInt()
        if (numRand1 > 0 && numRand2 <= 4) {
            resultado = numRand1 * numRand2
            binding.imOperando.setImageResource(R.drawable.multiplicacion)
        } else if(numRand2 == 0){
            nivel7()
        }
        else if(numRand1.toDouble() % numRand2.toDouble() != 0.0) {
            nivel7()
        }
        else{
            resultado = numRand1 / numRand2
            binding.ivNumUno.setImageResource(getDrawableId(numRand1))
            binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
            binding.imOperando.setImageResource(R.drawable.division)
        }
        binding.ivNumUno.setImageResource(getDrawableId(numRand1))
        binding.ivNumeroDos.setImageResource(getDrawableId(numRand2))
    }

    private fun nivel8(){
        toastMessage = Toast.makeText(requireContext(), "Último nivel", Toast.LENGTH_SHORT)
        toastMessage?.show()

        while (score < 100){
            nivel7()
        }
        vidas = 0;
        updateVidasUI()
        toastMessage = Toast.makeText(requireContext(), "¡Felicidades! Terminaste el juego.", Toast.LENGTH_SHORT)
        toastMessage?.show()

        val fragment = InitialFragment()
        val bundle = Bundle()
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}