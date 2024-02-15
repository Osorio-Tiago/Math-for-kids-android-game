package com.example.proyecto2movilessantiago

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.proyecto2movilessantiago.databinding.FragmentInitialBinding

class InitialFragment : Fragment() {


    //Linkeo de todos los objetos en el layout
    private lateinit var binding: FragmentInitialBinding


    //Media player y num rand
    private lateinit var mediaPlayer: MediaPlayer
    private var rand : Int = (Math.random() * 10).toInt()


    override fun onDestroyView() {
        super.onDestroyView()
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_initial, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInitialBinding.bind(view)

        binding.btnJugar.setOnClickListener { start() }

        val id = when (rand) {
            0, 10 -> resources.getIdentifier("mango", "drawable", requireContext().packageName)
            1, 9 -> resources.getIdentifier("fresa", "drawable", requireContext().packageName)
            2, 8 -> resources.getIdentifier("manzana", "drawable", requireContext().packageName)
            3, 7 -> resources.getIdentifier("sandia", "drawable", requireContext().packageName)
            else -> resources.getIdentifier("naranja", "drawable", requireContext().packageName)
        }
        binding.ivFruta.setImageResource(id)

        //Base de datos
        val admin = AdminSQLiteOpenHelper(requireContext(), "DB", null, 1)
        val db = admin.writableDatabase
        @SuppressLint("Recycle")
        val request = db.rawQuery(
            "select * from puntaje where score = (select max(score) from puntaje)",
            null
        )
        if (request.moveToFirst()) {
            val name = request.getString(0)
            val score = request.getString(1)

            binding.tvPuntuacion.text = "Record: $score de $name"
        }
        db.close()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }

    override fun onStart() {
        super.onStart()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mediaPlayer.isLooping = true;
        mediaPlayer.start();

    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun start() {
        val nickname = binding.etNombreJugador.text.toString()

        if (nickname.isNotEmpty()) {
            val fragment = LevelsFragment()
            val bundle = Bundle()
            bundle.putString("player", nickname)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()

            binding.etNombreJugador.setText("")
        } else {
            binding.etNombreJugador.error = "Ingresar un nombre para jugar"
            binding.etNombreJugador.requestFocus()
        }
    }
}