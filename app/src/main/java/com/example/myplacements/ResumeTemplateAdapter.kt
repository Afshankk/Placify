import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myplacements.R
import com.example.myplacements.model.ResumeTemplate

class ResumeTemplateAdapter(private val resumeTemplates: List<ResumeTemplate>) :
    RecyclerView.Adapter<ResumeTemplateAdapter.ResumeTemplateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumeTemplateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resume_template, parent, false)
        return ResumeTemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResumeTemplateViewHolder, position: Int) {
        val template = resumeTemplates[position]
        holder.bind(template)
    }

    override fun getItemCount(): Int = resumeTemplates.size

    class ResumeTemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_template)
        private val textView: TextView = itemView.findViewById(R.id.text_template_name)
        private val buttonView: Button = itemView.findViewById(R.id.button_view_template)

        fun bind(template: ResumeTemplate) {
            // Load the image using Glide
            Glide.with(itemView.context)
                .load(template.imageUrl)
                .into(imageView)

            // Set the template name
            textView.text = template.name

            // Set button click listener to open link
            buttonView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(template.link))
                itemView.context.startActivity(intent)
            }
        }
    }
}
