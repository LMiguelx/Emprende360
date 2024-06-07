package com.example.emprende360

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorEventosAsistidos(private val context: Context, private val eventosAsistidos: List<Map<String, Any>>) :
    RecyclerView.Adapter<AdaptadorEventosAsistidos.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoto: ImageView = view.findViewById(R.id.imgFoto)
        val lblNombreEvento: TextView = view.findViewById(R.id.lblNombreEvento)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_asistido, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = eventosAsistidos[position]
        holder.lblNombreEvento.text = evento["nombre"].toString()
        val imageUrl = evento["imagen"].toString()

        Glide.with(context) // Aquí se utiliza el contexto pasado como parámetro
            .load(imageUrl)
            .into(holder.imgFoto)

        val imageView: ImageView = holder.itemView.findViewById(R.id.imgIcono)

        // Hacemos la rotación de la imagen
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val matrix = Matrix()
        matrix.postRotate(-30f)
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        imageView.setImageBitmap(rotatedBitmap)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleEventoAsistidoActivity::class.java)
            intent.putExtra("evento", HashMap(evento))
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return eventosAsistidos.size
    }
}
