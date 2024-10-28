package com.example.myplacements

import ResumeTemplateAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myplacements.model.ResumeTemplate

class ResumeTemplatesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var resumeTemplateAdapter: ResumeTemplateAdapter

    // Sample data for resume templates
    private val resumeTemplates = listOf(
        ResumeTemplate(
            name = "Modern Resume",
            imageUrl = "https://writelatex.s3.amazonaws.com/published_ver/18551.jpeg?X-Amz-Expires=14400&X-Amz-Date=20241015T063912Z&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAWJBOALPNFPV7PVH5/20241015/us-east-1/s3/aws4_request&X-Amz-SignedHeaders=host&X-Amz-Signature=846035bc4714c13857f2d383ce44c7a5e809e7cf26abcfb9b4929dd1a43cf94b", // Replace with actual image URLs
            link = "https://www.overleaf.com/latex/templates/modern-latex-cv/qmdwjvcrcrph"
        ),
        ResumeTemplate(
            name = "Creative Resume",
            imageUrl = "https://writelatex.s3.amazonaws.com/published_ver/11039.jpeg?X-Amz-Expires=14400&X-Amz-Date=20241015T064018Z&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAWJBOALPNFPV7PVH5/20241015/us-east-1/s3/aws4_request&X-Amz-SignedHeaders=host&X-Amz-Signature=3303040954b6fe26c1ad061f4f7b594bbcf21d150bcdad2a137e97bda3693714", // Replace with actual image URLs
            link = "https://www.overleaf.com/latex/templates/awesome-cv/dfnvtnhzhhbm"
        ),
        ResumeTemplate(
            name = "Simple Resume",
            imageUrl = "https://writelatex.s3.amazonaws.com/published_ver/38460.jpeg?X-Amz-Expires=14400&X-Amz-Date=20241015T064130Z&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAWJBOALPNFPV7PVH5/20241015/us-east-1/s3/aws4_request&X-Amz-SignedHeaders=host&X-Amz-Signature=858468c997bd80c378fce92a866519ffd21ff4b608dee412287fab4ce029707b", // Replace with actual image URLs
            link = "https://www.overleaf.com/latex/templates/rendercv-engineeringresumes-theme/shwqvsxdgkjy"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_templates)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        resumeTemplateAdapter = ResumeTemplateAdapter(resumeTemplates)
        recyclerView.adapter = resumeTemplateAdapter
    }
}
